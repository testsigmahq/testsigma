/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.dto.RestStepResponseDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.HttpRequestMethod;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.web.request.RestStepRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class HttpClient {
  public final static String BASIC_AUTHORIZATION = "Basic";

  @Getter
  @Setter
  private String jwtApiKey;

  @Autowired
  public HttpClient() {
  }

  public static String getBasicAuthString(String s) {
    try {
      byte[] byteArray = s.getBytes();
      String auth;
      auth = Base64.encodeBase64String(byteArray);

      return auth;
    } catch (Exception ignore) {
      return "";
    }
  }

  public void closeConnection(CloseableHttpClient client) {
    HttpClientUtils.closeQuietly(client);
  }

  public RestStepResponseDTO execute(RestStepRequest restStepRequest) {
    log.info("Making a request to " + restStepRequest.toString());
    CloseableHttpClient client = getClient();
    RestStepResponseDTO restStepResponseDTO = null;
    try {
      HttpRequest request;
      StringEntity payload = new StringEntity("");
      if (restStepRequest.getPayload() != null) {
        payload = prepareBody(restStepRequest.getPayload());
      }
      if (restStepRequest.getRequestMethod().equals(HttpRequestMethod.POST)) {
        request = new HttpPost(restStepRequest.getUrl());
        ((HttpPost) request).setEntity(payload);
      } else if (restStepRequest.getRequestMethod().equals(HttpRequestMethod.PATCH)) {
        request = new HttpPatch(restStepRequest.getUrl());
        ((HttpPatch) request).setEntity(payload);
      } else if (restStepRequest.getRequestMethod().equals(HttpRequestMethod.DELETE)) {
        request = new HttpDelete(restStepRequest.getUrl());
      } else if (restStepRequest.getRequestMethod().equals(HttpRequestMethod.PUT)) {
        request = new HttpPut(restStepRequest.getUrl());
        ((HttpPut) request).setEntity(payload);
      } else if (restStepRequest.getRequestMethod().equals(HttpRequestMethod.HEAD)) {
        request = new HttpHead(restStepRequest.getUrl());
      } else {
        request = new HttpGet(restStepRequest.getUrl());
      }

      if (restStepRequest.getAuthorizationType() != null) {
        setAuthorizationHeaders(restStepRequest);
      }
      if (restStepRequest.getRequestHeaders() != null) {
        for (String key : restStepRequest.getRequestHeaders().keySet()) {
          String value = restStepRequest.getRequestHeaders().get(key);
          request.setHeader(key, value);
        }
      }

      HttpUriRequest httpUriRequest = HttpRequestWrapper.wrap(request);
      if (restStepRequest.getFollowRedirects() != null && !restStepRequest.getFollowRedirects()) {
        client = HttpClients.custom().disableRedirectHandling().build();
      }
      HttpResponse httpResponse = client.execute(httpUriRequest);
      HttpEntity responseEntity = httpResponse.getEntity();
      String responseStr = (responseEntity != null) ? EntityUtils.toString(httpResponse.getEntity()) : null;
      restStepResponseDTO = new RestStepResponseDTO(
        httpResponse.getStatusLine().getStatusCode(),
        responseStr,
        httpResponse.getAllHeaders()
      );
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      ;
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
    return restStepResponseDTO;
  }

  private StringEntity prepareBody(Object data) throws IOException {
    if (data.getClass().getName().equals("java.lang.String")) {
      return new StringEntity(data.toString(), "UTF-8");
    }
    String json = new ObjectMapperService().convertToJson(data);
    log.info("Request Data: " + json.substring(0, Math.min(json.length(), 1000)));
    return new StringEntity(json, "UTF-8");
  }

  private StringEntity prepareFormBody(Map<String, String> params) throws IOException {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (first)
        first = false;
      else
        result.append("&");
      result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    return new StringEntity(result.toString(), "UTF-8");
  }

  private void setAuthorizationHeaders(RestStepRequest entity) {

    if (entity.getAuthorizationType() != null) {
      Map<String, String> headers = entity.getRequestHeaders();
      headers = (headers != null) ? headers : new HashMap<>();
      Map<String, String> info = new ObjectMapperService().parseJson(entity.getAuthorizationValue(),
        new TypeReference<>() {
        });
      if (entity.getAuthorizationType().equals(1)) {
        headers.put("Authorization",
          "Basic " + getBasicAuthString(info.get("username") + ":" + info.get("password")));
      } else if (entity.getAuthorizationType() == 2) {
        headers.put("Authorization", "Bearer " + info.get("Bearertoken"));
      }
      entity.setRequestHeaders(headers);
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> post(String url, Object data,
                                                     TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a post request to " + url + " | with data - " + data.toString());
      HttpPost postRequest = new HttpPost(url);
      setHeaders(postRequest);
      postRequest.setEntity(prepareBody(data));
      HttpResponse res = client.execute(postRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> patch(String url, List<Header> httpHeaders, Object data,
                                                      TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a patch request to " + url + " | with data - " + data.toString());
      HttpPatch patchRequest = new HttpPatch(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      patchRequest.setHeaders(itemsArray);
      patchRequest.setEntity(prepareBody(data));
      HttpResponse res = client.execute(patchRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> postAndStoreCookies(String url, List<Header> httpHeaders, Object data,
                                                                    TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a post request to " + url + " | with data - " + data.toString());
      HttpPost postRequest = new HttpPost(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      postRequest.setHeaders(itemsArray);
      postRequest.setEntity(prepareBody(data));

      HttpClientContext context = HttpClientContext.create();
      HttpResponse res = client.execute(postRequest, context);
      CookieStore cookieStore = context.getCookieStore();
      List<Cookie> cookies = cookieStore.getCookies();
      return new com.testsigma.util.HttpResponse<>(res, typeReference, cookies);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> post(String url, List<Header> httpHeaders, Object data,
                                                     TypeReference<T> typeReference)
    throws TestsigmaException {
    return this.post(url, httpHeaders, data, typeReference, null);
  }

  public <T> com.testsigma.util.HttpResponse<T> formPost(String url, List<Header> httpHeaders, HashMap<String, String> data,
                                                         TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a post request to " + url + " | with data - " + data.toString());
      HttpPost postRequest = new HttpPost(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      postRequest.setHeaders(itemsArray);
      postRequest.setEntity(prepareFormBody(data));
      HttpResponse res = client.execute(postRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> put(String url, List<Header> httpHeaders, Object data,
                                                    TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a put request to " + url + " | with data - " + data.toString());
      HttpPut putRequest = new HttpPut(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      putRequest.setHeaders(itemsArray);
      putRequest.setEntity(prepareBody(data));
      HttpResponse res = client.execute(putRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> get(List<Header> httpHeaders, URIBuilder builder,
                                                    TypeReference<T> typeReference)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      HttpGet getRequest = new HttpGet(builder.build());
      getRequest.setHeaders(itemsArray);
      HttpResponse res = client.execute(getRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException | URISyntaxException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> get(String url, List<Header> httpHeaders,
                                                    TypeReference<T> typeReference)
    throws TestsigmaException {
    return this.get(url, httpHeaders, typeReference, null);
  }

  public <T> com.testsigma.util.HttpResponse<T> delete(String url, List<Header> httpHeaders,
                                                       TypeReference<T> typeReference)
    throws TestsigmaException {
    return this.delete(url, httpHeaders, typeReference, null);
  }

  public <T> com.testsigma.util.HttpResponse<T> delete(String url, List<Header> httpHeaders,
                                                       TypeReference<T> typeReference, BasicCookieStore cookieStore)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a delete request to " + url + " | with headers - " + httpHeaders);
      HttpDelete delRequest = new HttpDelete(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      delRequest.setHeaders(itemsArray);
      HttpResponse res;
      if (cookieStore != null) {
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        res = client.execute(delRequest, localContext);
      } else
        res = client.execute(delRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> post(String url, List<Header> httpHeaders, Object data,
                                                     TypeReference<T> typeReference, BasicCookieStore cookieStore)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a post request to " + url + " | with data - " + data.toString());
      HttpPost postRequest = new HttpPost(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      postRequest.setHeaders(itemsArray);
      postRequest.setEntity(prepareBody(data));
      HttpResponse res;
      if (cookieStore != null) {
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        res = client.execute(postRequest, localContext);
      } else
        res = client.execute(postRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> get(String url, List<Header> httpHeaders,
                                                    TypeReference<T> typeReference, BasicCookieStore cookieStore)
    throws TestsigmaException {
    CloseableHttpClient client = getClient();
    try {
      log.info("Making a Get request to " + url + " | with headers - " + httpHeaders);
      HttpGet getRequest = new HttpGet(url);
      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      getRequest.setHeaders(itemsArray);
      HttpResponse res;
      if (cookieStore != null) {
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        res = client.execute(getRequest, localContext);
      } else
        res = client.execute(getRequest);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public <T> com.testsigma.util.HttpResponse<T> post(String url, List<Header> httpHeaders,
                                                     String fileName, InputStream is,
                                                     TypeReference<T> typeReference, BasicCookieStore cookieStore) throws TestsigmaException {
    log.info("Making a Post request to " + url + " | with file - " + fileName);
    CloseableHttpClient client = getClient();
    try {
      HttpEntity entity = MultipartEntityBuilder
        .create()
        .addBinaryBody("file", is, ContentType.create("application/octet-stream"), fileName)
        .build();

      HttpPost httpPost = new HttpPost(url);
      httpPost.setEntity(entity);

      Header[] itemsArray = new Header[httpHeaders.size()];
      itemsArray = httpHeaders.toArray(itemsArray);
      httpPost.setHeaders(itemsArray);

      if (cookieStore != null) {
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
      }

      HttpResponse res = client.execute(httpPost);
      return new com.testsigma.util.HttpResponse<T>(res, typeReference);
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  public HttpResponse downloadFile(String url, String filePath, Map<String, String> headers) throws IOException {
    log.info("Making a download file request to " + url);
    CloseableHttpClient client = getClient();
    try {
      HttpGet getRequest = new HttpGet(url);
      for (String key : headers.keySet()) {
        getRequest.addHeader(key, headers.get(key));
      }

      getRequest.setHeader("Accept", "*/*");

      HttpResponse res = client.execute(getRequest);

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

      return res;
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }

  private void setHeaders(HttpRequestBase getRequest) {
    if (this.jwtApiKey != null) {
      getRequest.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + jwtApiKey);
    }
    getRequest.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
  }

  public synchronized CloseableHttpClient getClient() {
    RequestConfig config = RequestConfig.custom()
      .setCookieSpec(CookieSpecs.STANDARD)
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(2 * 60 * 1000)
      .build();
    return HttpClients.custom().setDefaultRequestConfig(config).build();
  }

  public HttpResponse downloadRedirectFile(String url, String filePath, Map<String, String> headers) throws IOException {
    log.info("Making a download file request to " + url);
    CloseableHttpClient client = getClientRedirect();
    return downloadFile(client, url, filePath, headers);
  }

  public synchronized CloseableHttpClient getClientRedirect() {
    RequestConfig config = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD)
            .setSocketTimeout(10 * 60 * 1000)
            .setConnectionRequestTimeout(60 * 1000)
            .setConnectTimeout(2 * 60 * 1000)
            .build();
    return HttpClients.custom().setDefaultRequestConfig(config).setRedirectStrategy(new LaxRedirectStrategy()).build();
  }

  public HttpResponse downloadFile(CloseableHttpClient client, String url, String filePath, Map<String, String> headers) throws IOException {
    log.info("Making a download file request to " + url);
    try {
      HttpGet getRequest = new HttpGet(url);
      for (String key : headers.keySet()) {
        getRequest.addHeader(key, headers.get(key));
      }

      getRequest.setHeader("Accept", "*/*");

      HttpResponse res = client.execute(getRequest);

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
      return res;
    } finally {
      if (client != null) {
        closeConnection(client);
      }
    }
  }
}
