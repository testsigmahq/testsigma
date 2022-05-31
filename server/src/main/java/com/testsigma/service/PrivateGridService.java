package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.PrivateGridNodeMapper;
import com.testsigma.model.*;
import com.testsigma.repository.PrivateGridNodeRepository;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.IntegrationsRequest;
import com.testsigma.web.request.PrivateGridBrowserRequest;
import com.testsigma.web.request.PrivateGridNodeRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class PrivateGridService {
    private final HttpClient httpClient;
    private final ApplicationConfig config;
    private final PrivateGridNodeMapper nodeMapper;
    private final PrivateGridNodeRepository repository;

    @Getter
    @Setter
    private Integrations applicationConfig;

    private void fetchBrowsersFromNode(String proxy, String gridURL) throws TestsigmaException {
        HttpResponse<JsonNode> response = httpClient.get(gridURL + "/grid/api/proxy?id=" + proxy, getHeaders(), new TypeReference<JsonNode>() {
        });
        try {
            JsonNode browsers = response.getResponseEntity().get("request").get("configuration").get("capabilities");
            for (JsonNode browser : browsers) {
                ((ObjectNode) browser).put("browserName", StringUtils.capitalize(browser.get("browserName").asText().toLowerCase().replaceAll("\\s", "")));
                ((ObjectNode) browser).put("platform", StringUtils.capitalize(browser.get("platform").asText().toLowerCase().replaceAll("\\s", "")));
                if (browser.get("platform").asText().contains("Win"))
                    ((ObjectNode) browser).put("platform", "Windows");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PrivateGridBrowserRequest[] browsersList = mapper.convertValue(browsers, PrivateGridBrowserRequest[].class);
            PrivateGridNodeRequest request = new PrivateGridNodeRequest();
            request.setNodeName(proxy);
            request.setGridURL(gridURL);
            request.setBrowserList(List.of(browsersList));
            PrivateGridNode node = nodeMapper.map(request);
            this.create(node);
        } catch (Exception e) {
            throw new TestsigmaException("Unable extract and save the node configurations from your private grid");
        }
    }

    public List<String> ParseProxyIds(String gridUrl) throws TestsigmaException {
        HttpResponse<JsonNode> response = httpClient.get(gridUrl + "/grid/console", getHeaders(), new TypeReference<JsonNode>() {
        });

        Document doc = Jsoup.parse(response.toString());
        Elements proxies = doc.select("p.proxyid");
        // "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        List<String> parsedURLs = new ArrayList<String>();

        try {
            Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(proxies.text());
            while (matcher.find()) {
                String URL = proxies.text().substring(matcher.start(0), matcher.end(0));
                if (!parsedURLs.contains(URL)) {
                    parsedURLs.add(URL);
                    this.fetchBrowsersFromNode(URL, gridUrl);
                }
            }
            if (!(parsedURLs.size() > 0)) {
                log.error(" No URL found with the given regex in the response message.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof TestsigmaException)
                throw new TestsigmaException(e.getMessage());
            else
                throw new TestsigmaException(" : URLs extraction failed - " + e.getMessage());
        }
        return parsedURLs;
    }


    public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
        HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl(), getHeaders(), new TypeReference<JsonNode>() {
        });

        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode status = jnf.objectNode();
        status.put("status_code", response.getStatusCode());
        status.put("status_message", response.getStatusMessage());
        if (response.getStatusCode() == HttpStatus.SC_OK)
            this.ParseProxyIds(testAuth.getUrl());
        return status;
    }

    private List<Header> getHeaders() {
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return Lists.newArrayList(contentType);
    }

    public PrivateGridNode create(PrivateGridNode node) {
        return this.repository.save(node);
    }

    public void cleanTable() {
        this.repository.deleteAll();
    }


    public List<PrivateGridNode> findAll() {
        return this.repository.findAll();
    }

    public List<Platform> getAllPlatforms() {
        List<PrivateGridNode> nodes = this.repository.findAll();
        return nodes.stream().flatMap(node -> node.getBrowserList().stream()).map(PrivateGridBrowser::getPlatform).distinct().collect(Collectors.toList());
    }

    public List<Browsers> getPlatformSupportedBrowsers(Platform platform) {
        List<PrivateGridNode> nodes = this.repository.findAll();
        List<PrivateGridBrowser> browsers = nodes.stream().flatMap(node -> node.getBrowserList().stream())
                .filter(privateGridBrowser -> privateGridBrowser.getPlatform() == platform)
                .distinct().collect(Collectors.toList());
        return mapGridBrowsersToBrowsers(browsers);

    }

    private List<Browsers> mapGridBrowsersToBrowsers(List<PrivateGridBrowser> browsers) {
        List<Browsers> browsersList = new ArrayList<>();
        for (PrivateGridBrowser browser : browsers) {
            browsersList.add(Browsers.getBrowser(browser.getBrowserName().getHybridName()));
        }
        return browsersList;
    }

    public List<PlatformBrowserVersion> getPlatformBrowserVersions(Platform platform, Browsers browserName) {
        List<PrivateGridNode> nodes = this.repository.findAll();
        List<PrivateGridBrowser> browsers = nodes.stream().flatMap(node -> node.getBrowserList().stream())
                .filter(privateGridBrowser -> privateGridBrowser.getPlatform() == platform && Objects.equals(privateGridBrowser.getBrowserName().getHybridName(), browserName.getKey()))
                .distinct().collect(Collectors.toList());
        List<String> versions = browsers.stream().map(PrivateGridBrowser::getVersion).collect(Collectors.toList());
        List<PlatformBrowserVersion> platformBrowserVersions = new ArrayList<>();
        for (String version : versions) {
            PlatformBrowserVersion platformBrowserVersion = new PlatformBrowserVersion();
            platformBrowserVersion.setPlatform(platform);
            platformBrowserVersion.setVersion(version);
            platformBrowserVersion.setDisplayVersion(version);
            platformBrowserVersion.setName(browserName);
        }
        return platformBrowserVersions;
    }
}
