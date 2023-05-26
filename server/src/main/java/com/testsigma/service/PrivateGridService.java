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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        List<Header> headers = getHeaders();
        JSONObject query = new JSONObject();
        query.put("query", "{ nodesInfo { nodes { stereotypes } } }");
        HttpResponse<JsonNode> response = httpClient.post(gridURL + "/graphql",
                headers, query.toString(), new TypeReference<>() {
                });
        try {
            JsonNode nodes = response.getResponseEntity().get("data").get("nodesInfo").get("nodes");
            JSONArray validPlatforms = new JSONArray();
            JSONObject browserDetails;
            if (!nodes.isEmpty()) {
                    for (JsonNode node : nodes) {
                        String stereotypes = node.get("stereotypes").asText().replaceAll("\n", "");
                        JSONArray nodeStereotypes = new JSONArray(stereotypes);
                        for (int i = 0; i < nodeStereotypes.length(); i++) {
                            JSONObject nodeDetails = (JSONObject) nodeStereotypes.get(i);
                            JSONObject stereotype = (JSONObject) nodeDetails.get("stereotype");
                            browserDetails = new JSONObject();
                            browserDetails.put("browserName", StringUtils.capitalize(stereotype.get("browserName").toString().toLowerCase().replaceAll("\\s", "")));
                            browserDetails.put("maxInstances",Integer.parseInt(nodeDetails.get("slots").toString()));
//                            if (OSBrowserType.getBrowserEnumValueIfExists(String.valueOf(browserDetails.get("browserName")))==null){
//                                continue;
//                            }
                            if (String.valueOf(stereotype.get("platformName")).contains("Win") || String.valueOf(stereotype.get("platformName")).contains("WIN"))
                            {
                                browserDetails.put("platform", "Windows");
                                browserDetails.put("platformName", "Windows");
                            } else
                            {
                                browserDetails.put("platform", StringUtils.capitalize(stereotype.get("platformName").toString().toLowerCase().replaceAll("\\s", "")));
                                browserDetails.put("platformName", StringUtils.capitalize(stereotype.get("platformName").toString().toLowerCase().replaceAll("\\s", "")));
                            }
                            validPlatforms.put(browserDetails);
                        }
                    }
                }
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PrivateGridBrowserRequest[] browsersList = new ObjectMapperService().parseJson(validPlatforms.toString(), PrivateGridBrowserRequest[].class);
//            PrivateGridBrowserRequest[] browsersList = mapper.convertValue(validPlatforms, PrivateGridBrowserRequest[].class);
            PrivateGridNodeRequest request = new PrivateGridNodeRequest();
            request.setNodeName(proxy);
            request.setGridURL(gridURL);
            request.setBrowserList(List.of(browsersList));
            PrivateGridNode node = nodeMapper.map(request);
            if (node.getBrowserList().size()<1)
                throw new TestsigmaException("Node configuration is not correct! may be unsupported browsers and platforms are added");
            this.create(node);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            if (e instanceof TestsigmaException)
                throw new TestsigmaException(e.getMessage());
            else
            throw new TestsigmaException("Unable extract and save the node configurations from your private grid");
        }
    }

    public List<String> ParseProxyIds(String gridUrl) throws TestsigmaException {

        List<Header> headers = getHeaders();
        JSONObject query = new JSONObject();
        query.put("query", "{ nodesInfo { nodes { uri } } }");
        HttpResponse<JsonNode> response = httpClient.post(gridUrl + "/graphql",
                headers, query.toString(), new TypeReference<>() {
                });

        JsonNode proxies = response.getResponseEntity().get("data").get("nodesInfo").get("nodes");
        List<String> parsedURLs = new ArrayList<String>();

        try {
            if (!proxies.isEmpty()) {
                for (JsonNode proxy : proxies) {
                    String url = proxy.get("uri").asText();
                    if (!parsedURLs.contains(url)) {
                        parsedURLs.add(url);
                        this.fetchBrowsersFromNode(url, gridUrl);
                    }
                }
                if (!(parsedURLs.size() > 0)) {
                    log.error(" No URL found with the given regex in the response message.");
                }
            }
        }
        catch (Exception e) {
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
