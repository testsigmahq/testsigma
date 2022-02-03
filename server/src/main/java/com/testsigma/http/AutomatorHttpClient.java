package com.testsigma.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testsigma.automator.http.HttpClient;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Set;

@Log4j2
@Component
public class AutomatorHttpClient extends HttpClient {
  private final CloseableHttpClient httpClient;
  private final PoolingHttpClientConnectionManager cm;

  @Autowired
  public AutomatorHttpClient() {
    cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(800);
    cm.setDefaultMaxPerRoute(300);
    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(60 * 1000)
      .build();
    httpClient = HttpClients.custom().setDefaultRequestConfig(config)
      .setConnectionManager(cm)
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
    printConnectionPoolStats();
    log.info("Making a get request to " + url);
    log.info("Auth Header passed is - " + authHeader);

    HttpGet getRequest = new HttpGet(url);
    if (authHeader != null) {
      getRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
    }
    getRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    HttpResponse res = httpClient.execute(getRequest);
    return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> put(String url, Object data,
                                                              TypeReference<T> typeReference, String authHeader)
    throws IOException {
    printConnectionPoolStats();
    log.info("Making a put request to " + url + " | with data - " + data.toString());
    log.info("Auth Header passed is - " + authHeader);

    HttpPut putRequest = new HttpPut(url);
    if (authHeader != null) {
      putRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
    }
    putRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    putRequest.setEntity(prepareBody(data));
    HttpResponse res = httpClient.execute(putRequest);
    return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> post(String url, Object data,
                                                               TypeReference<T> typeReference, String authHeader)
    throws IOException {
    printConnectionPoolStats();
    log.info("Making a post request to " + url + " | with data - " + data.toString());
    log.info("Auth Header passed is - " + authHeader);

    HttpPost postRequest = new HttpPost(url);
    if (authHeader != null) {
      postRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
    }
    postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    postRequest.setEntity(prepareBody(data));
    HttpResponse res = httpClient.execute(postRequest);
    return new com.testsigma.automator.http.HttpResponse<T>(res, typeReference);
  }

  public <T> com.testsigma.automator.http.HttpResponse<T> downloadFile(String url, String filePath, String authHeader) throws IOException {
    printConnectionPoolStats();
    log.info("Making a download file request to " + url);
    log.info("Auth Header passed is - " + authHeader);

    HttpGet getRequest = new HttpGet(url);
    if (authHeader != null) {
      getRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, authHeader);
    }
    getRequest.setHeader(HttpHeaders.ACCEPT, "*/*");
    getRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    HttpResponse res = httpClient.execute(getRequest);

    Integer status = res.getStatusLine().getStatusCode();
    log.info("Download file request response code - " + status);
    if (status.equals(HttpStatus.OK.value())) {
      BufferedInputStream bis = new BufferedInputStream(res.getEntity().getContent());
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
      int inByte;
      while ((inByte = bis.read()) != -1) bos.write(inByte);
      bis.close();
      bos.close();
    }

    return new com.testsigma.automator.http.HttpResponse<T>(res);

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

  private void printConnectionPoolStats() {
    try {
      Set<HttpRoute> routes = cm.getRoutes();
      for (HttpRoute route : routes) {
        PoolStats stats = cm.getStats(route);
        log.info(String.format("[Host: %s] -> [Leased: %s] [Pending: %s] [Available: %s] [Max: %s]",
          route.getTargetHost().getHostName(), stats.getLeased(), stats.getPending(), stats.getAvailable(),
          stats.getMax()));
      }
    } catch (Exception ignored) {
    }
  }

  public void closeHttpClient() {
    try {
      log.debug("Closing HTTPClient of id: " + this.httpClient.hashCode());
      this.httpClient.close();
      this.cm.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
