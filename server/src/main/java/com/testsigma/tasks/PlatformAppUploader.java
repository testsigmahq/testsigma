package com.testsigma.tasks;

import com.testsigma.service.TestsigmaOSConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;


@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformAppUploader {
  private final TestsigmaOSConfigService testsigmaOSConfigService;


  public String uploadAppToTestsigmaLab(String accessKey, String appFileLocalPath) {
    try {
      String fileName = Paths.get(appFileLocalPath).getFileName().toString();
      File appFile = new File(appFileLocalPath);
      HttpClient client = HttpClients.custom().build();
      HttpPost request = new HttpPost(testsigmaOSConfigService.getUrl() + "/api/uploads");
      request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + accessKey);

      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      builder.addPart("fileContent", new FileBody(appFile, ContentType.DEFAULT_BINARY));
      builder.addPart("name", new StringBody(fileName, ContentType.MULTIPART_FORM_DATA));
      HttpEntity entity = builder.build();
      request.setEntity(entity);

      HttpResponse response = client.execute(request);
      log.info("Response from testsigma lab post uploading - " + response);
      if (response.getStatusLine().getStatusCode() < HttpStatus.SC_MULTIPLE_CHOICES) {
        String appId = EntityUtils.toString(response.getEntity());
        log.info("Testsigma Lab App ID Post Upload - " + appId);
        return appId;
      } else {
        return null;
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

}

