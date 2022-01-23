package com.testsigma.automator.storage;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;

@Log4j2
public class OnPremiseUploader extends StorageUploader {

  @Override
  public void upload(String srcFilePath, String destinationURL) {
    if (!isFileExists(srcFilePath)) {
      log.info("File not found. Unable to upload - " + srcFilePath);
      return;
    }

    log.info(String.format("Uploading test asset to On premise storage, presigned-URL:%s, localFilePath:%s", destinationURL, srcFilePath));
    HttpResponse response = null;
    try {
      File uploadFile = new File(srcFilePath);
      HttpClient httpclient = createHttpClient();
      HttpPost httppost = new HttpPost(destinationURL);
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.addPart("file", new FileBody(uploadFile));
      HttpEntity entity = builder.build();
      httppost.setEntity(entity);
      response = httpclient.execute(httppost);
      log.info("Storage upload Response :" + response);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {

    }
  }

  private CloseableHttpClient createHttpClient() {
    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(60 * 1000)
      .build();
    return HttpClients.custom().setDefaultRequestConfig(config)
      .build();
  }
}
