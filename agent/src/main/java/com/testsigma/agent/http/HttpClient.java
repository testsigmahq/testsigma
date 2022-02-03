/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;

import javax.annotation.PreDestroy;
import java.io.*;

@Log4j2
public class HttpClient extends com.testsigma.automator.http.HttpClient {
  public final static String BEARER = "Bearer";
  private final CloseableHttpClient httpClient;

  public HttpClient() {
    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(60 * 1000)
      .build();
    this.httpClient = HttpClients.custom().setDefaultRequestConfig(config)
      .build();
  }

  @PreDestroy
  public void closeConnection() {
    HttpClientUtils.closeQuietly(this.httpClient);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> get(String url, TypeReference<T> typeReference)
    throws IOException {
    return get(url, typeReference, null);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> put(String url, Object data,
                                                              TypeReference<T> typeReference)
    throws IOException {
    return put(url, data, typeReference, null);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> post(String url, Object data,
                                                               TypeReference<T> typeReference)
    throws IOException {
    return post(url, data, typeReference, null);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> downloadFile(String url, String filePath) throws IOException {
    return downloadFile(url, filePath, null);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> get(String url, TypeReference<T> typeReference,
                                                              String authHeader) throws IOException {
    log.info("Making a get request to " + url);

    CloseableHttpClient client = getClient();
    try {
      HttpGet getRequest = new HttpGet(url);
      if (authHeader != null) {
        getRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
      }
      getRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
      HttpResponse res = client.execute(getRequest);
      return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
    } finally {
      if (client != null) {
        HttpClientUtils.closeQuietly(client);
      }
    }
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> put(String url, Object data,
                                                              TypeReference<T> typeReference, String authHeader)
    throws IOException {
    log.info("Making a put request to " + url + " | with data - " + data.toString());

    CloseableHttpClient client = getClient();
    try {
      HttpPut putRequest = new HttpPut(url);
      if (authHeader != null) {
        putRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
      }
      putRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
      putRequest.setEntity(prepareBody(data));
      HttpResponse res = client.execute(putRequest);
      return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
    } finally {
      if (client != null) {
        HttpClientUtils.closeQuietly(client);
      }
    }
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> post(String url, Object data,
                                                               TypeReference<T> typeReference, String authHeader)
    throws IOException {
    log.info("Making a post request to " + url + " | with data - " + data.toString());

    CloseableHttpClient client = getClient();
    try {
      HttpPost postRequest = new HttpPost(url);
      if (authHeader != null) {
        postRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
      }
      postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
      postRequest.setEntity(prepareBody(data));
      HttpResponse res = client.execute(postRequest);
      return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
    } finally {
      if (client != null) {
        HttpClientUtils.closeQuietly(client);
      }
    }
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> downloadFile(String url, String filePath, String authHeader) throws IOException {

    log.info("Making a get request to " + url);
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    HttpResponse res;
    CloseableHttpClient client = getClient();

    try {
      HttpGet getRequest = new HttpGet(url);
      if (authHeader != null) {
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
      }
      getRequest.setHeader(HttpHeaders.ACCEPT, "*/*");
      getRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
      res = client.execute(getRequest);

      Integer status = res.getStatusLine().getStatusCode();
      log.info("Download file request response code - " + status);
      if (status.equals(HttpStatus.OK.value())) {
        bis = new BufferedInputStream(res.getEntity().getContent());
        bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        int inByte;
        while ((inByte = bis.read()) != -1) bos.write(inByte);
      }
      return new com.testsigma.automator.http.HttpResponse<T>(res);
    } finally {
      if (client != null) {
        HttpClientUtils.closeQuietly(client);
      }
      assert (bos != null);
      bos.close();
      bis.close();
    }
  }

  private StringEntity prepareBody(Object data) throws IOException {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    if (data.getClass().getName().equals("java.lang.String")) {
      return new StringEntity(data.toString(), "UTF-8");
    }
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    String json = mapper.writeValueAsString(data);
    log.info("Request Data: " + json.substring(0, Math.min(json.length(), 1000)));
    return new StringEntity(json, "UTF-8");
  }

  public CloseableHttpClient getClient() {
    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(60 * 1000)
      .build();
    return HttpClients.custom().setDefaultRequestConfig(config).build();
  }

  public void closeHttpClient() {
    try {
      log.debug("Closing HTTPClient of id: " + this.httpClient.hashCode());
      if (this.httpClient != null) {
        HttpClientUtils.closeQuietly(this.httpClient);
      }
    } catch (Exception e) {
      log.error("Error while closing HttpClient");
      log.error(e.getMessage(), e);
    }
  }
}
