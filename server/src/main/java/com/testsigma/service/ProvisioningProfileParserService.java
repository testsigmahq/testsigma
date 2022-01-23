package com.testsigma.service;


import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.google.common.collect.Lists;
import com.testsigma.exception.TestsigmaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProvisioningProfileParserService {

  private final TestsigmaOSConfigService osService;
  private String provisioningUrl = null;

  @PostConstruct
  public void init() {
    provisioningUrl = osService.getTestsigmaOsProxyUrl() + "/api_public/ios/provisioning/";
  }

  public List<String> parseDevices(File provisioningProfile) throws TestsigmaException, IOException {
    String parsedProfileString = parseProvisioningProfile(provisioningProfile);
    try {
      List<String> deviceUDIDs = new ArrayList<>();
      log.info("response while parsing provisioned profile - " + parsedProfileString);
      try {
        NSDictionary parsedProfile = (NSDictionary) PropertyListParser.parse(parsedProfileString.getBytes(StandardCharsets.UTF_8));
        deviceUDIDs = parsedProfile.get("ProvisionedDevices").toJavaObject(deviceUDIDs.getClass());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }

      return deviceUDIDs;
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }

  public String getTeamId(File provisioningProfile) throws IOException, TestsigmaException {
    String parsedProfileString = parseProvisioningProfile(provisioningProfile);
    try {
      log.info("response while parsing provisioned profile - " + parsedProfileString);
      String teamId = "";
      try {
        NSDictionary parsedProfile = (NSDictionary) PropertyListParser.parse(parsedProfileString.getBytes(StandardCharsets.UTF_8));
        NSDictionary entitlements = ((NSDictionary) parsedProfile.get("Entitlements"));
        teamId = entitlements.get("com.apple.developer.team-identifier").toString();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
      return teamId;
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }

  public String parseProvisioningProfile(File provisioningProfile) throws IOException, TestsigmaException {
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    //Creating this temp file, because while executing postRequest the .mobileprovision file in /tmp is deleting
    //So creating a temp file and using that file for sending http request
    File tempFile = File.createTempFile("temp", CertificateService.MOBILE_PROVISION_EXTENSION);
    FileUtils.copyFile(provisioningProfile, tempFile);
    builder.addPart("provisioningProfile", new FileBody(tempFile, ContentType.DEFAULT_BINARY));
    HttpResponse response = postRequest(provisioningUrl + "parse", builder.build());
    int status = response.getStatusLine().getStatusCode();
    if (status == HttpStatus.OK.value()) {
      BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      br.close();
      return sb.toString();
    } else {
      throw new TestsigmaException("Error while parsing Provisioning Profile");
    }
  }

  public HttpResponse postRequest(String url, HttpEntity body) throws IOException {
    CloseableHttpClient closeableHttpClient = HttpClients.custom().build();
    Header accept = new BasicHeader(HttpHeaders.ACCEPT, "*/*");
    List<Header> headers = Lists.newArrayList(accept);
    HttpPost postRequest = new HttpPost(url);
    postRequest.setHeaders(headers.toArray(Header[]::new));
    postRequest.setEntity(body);
    return closeableHttpClient.execute(postRequest);
  }
}

