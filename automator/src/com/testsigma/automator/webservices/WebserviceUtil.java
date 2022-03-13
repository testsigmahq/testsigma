package com.testsigma.automator.webservices;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.PathNotFoundException;
import com.testsigma.automator.constants.AuthorizationTypes;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.TestsigmaFileNotFoundException;
import com.testsigma.automator.exceptions.TestsigmaTestdataNotFoundException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.runners.EnvironmentRunner;
import com.testsigma.automator.service.ObjectMapperService;
import com.testsigma.automator.utilities.PathUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class WebserviceUtil {

  private static final String HTTP = "http";
  private static final String HTTPS = "https";
  private HttpClient httpclient;
  private static int CONNECTION_TIMEOUT = 10 * 60 * 1000;
  private static int SOCKET_TIMEOUT = 10 * 60 * 1000;

  public void execute(TestCaseStepEntity testcaseStep, TestCaseStepResult result, Map<String, String> envSettings, TestCaseResult testCaseResult) throws TestsigmaTestdataNotFoundException {
    log.debug("Executing Rest step:" + testcaseStep);
    RestfulStepEntity entity = new ObjectMapper().convertValue(testcaseStep.getAdditionalData()
      .get(TestCaseStepEntity.REST_DETAILS_KEY), RestfulStepEntity.class);
    result.setResult(ResultConstant.SUCCESS);
    try {
      log.debug("Updating Rest step variables for RestStepEntity:" + entity);
      new RestAPIRunTimeDataProcessor(entity,result).processRestAPIStep();

      initializeHttpClient(entity);
      Map<String, String> headers = fetchHeadersFromRestStep(entity);
      setAuthorizationHeaders(entity, headers);
      setDefaultHeaders(entity, headers);
      entity.setUrl(entity.getUrl().replace("\\", "/"));
      HttpResponse<String> response = executeRestCall(entity.getUrl(),
        RequestMethod.valueOf(entity.getMethod().toUpperCase()), headers, getEntity(entity, envSettings, headers));

      log.debug("Rest Url - " + entity.getUrl() + " Method " + entity.getMethod().toUpperCase()
        + " Headers - " + new ObjectMapperService().convertToJson(headers) + " PayLoad - " + entity.getPayload());
      result.getMetadata().setReqEntity(entity);

      log.debug("Method - " + entity.getMethod().toUpperCase() + "response - " + new ObjectMapperService().convertToJson(response));
      ((Map<String, Object>) (testcaseStep.getAdditionalData().get(TestCaseStepEntity.REST_DETAILS_KEY))).put("url", entity.getUrl());

      WebserviceResponse resObj = new WebserviceResponse();
      resObj.setStatus(response.getStatusCode());

      if (entity.getStoreMetadata()) {
        resObj.setContent(response.getResponseText());
        resObj.setHeaders(response.getHeadersMap());
      }
      result.getMetadata().setRestResult(resObj);

      new RestApiResponseValidator(entity, result, response).validateResponse();
      new RestAPIRunTimeDataProcessor(entity,result).storeResponseData(response);

    } catch (Exception e) {
      log.error("Error while executing Rest Step:"+testcaseStep, e);
      String genericExceptionMessage = getExceptionSpecificMessage(e,result);
      result.setResult(ResultConstant.FAILURE);
      result.setMessage(genericExceptionMessage);
    }

    log.debug("Test Step Result :: " + new ObjectMapperService().convertToJson(result));
  }

  private String getExceptionSpecificMessage(Exception exception, TestCaseStepResult testCaseStepResult) {
    String resultMessage;
    if(testCaseStepResult.getMessage() != null){//If specific Message is already set, we show the same in result
      return testCaseStepResult.getMessage();
    }
    if (exception instanceof PathNotFoundException) {
      resultMessage = AutomatorMessages.MSG_INVALID_PATH;
    } else if (exception instanceof ClientProtocolException) {
      if (exception.getCause() instanceof CircularRedirectException) {
        resultMessage = exception.getCause().getLocalizedMessage();
      } else {
        resultMessage = exception.getLocalizedMessage();
      }
    } else if (exception.getCause() instanceof ProtocolException) {
      resultMessage = AutomatorMessages.MSG_REST_INVALID_URL;
    } else if (exception instanceof UnknownHostException) {
      resultMessage = AutomatorMessages.MSG_INVALID_URL;
    } else {
      resultMessage = exception.getLocalizedMessage();
    }
    return resultMessage;
  }

  private Map<String, String> fetchHeadersFromRestStep(RestfulStepEntity restfulStepEntity) {
    Map<String, String> headers = new HashMap<>();
    if (StringUtils.isNotBlank(restfulStepEntity.getRequestHeaders())) {
      headers = new ObjectMapperService().parseJson(restfulStepEntity.getRequestHeaders(), new TypeReference<>() {
      });
    }
    return headers;
  }

  private void initializeHttpClient(RestfulStepEntity restfulStepEntity) {
    RequestConfig config = RequestConfig.custom()
      .setConnectTimeout(CONNECTION_TIMEOUT)
      .setSocketTimeout(SOCKET_TIMEOUT).build();
    if (restfulStepEntity.getFollowRedirects()) {
      httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
    } else {
      httpclient = HttpClients.custom().setDefaultRequestConfig(config).disableRedirectHandling().build();
    }
  }

  private void setAuthorizationHeaders(RestfulStepEntity entity, Map<String, String> headers) {
    log.debug("Set Authorization headers for entity:" + entity);
    log.debug("Set Authorization headers ,headers:" + headers);
    if (entity.getAuthorizationType() != null) {
      headers = (headers != null) ? headers : new HashMap<>();
      Map<String, String> info = new ObjectMapperService().parseJson(entity.getAuthorizationValue(),
        new TypeReference<>() {
        });
      if (AuthorizationTypes.BASIC == entity.getAuthorizationType()) {
        headers.put(HttpHeaders.AUTHORIZATION,
          com.testsigma.automator.http.HttpClient.BASIC_AUTHORIZATION + " " +
            encodedCredentials(info.get("username") + ":" + info.get("password")));
      } else if (entity.getAuthorizationType() == AuthorizationTypes.BEARER) {
        headers.put("Authorization", "Bearer " + info.get("Bearertoken"));
      }
    }
  }

  private String encodedCredentials(String input) {
    String authHeader = "";
    try {
      byte[] encodedAuth = Base64.encodeBase64(input.getBytes(StandardCharsets.ISO_8859_1));
      if (encodedAuth != null) {
        authHeader = new String(encodedAuth);
      }
    } catch (Exception ignore) {
    }
    return authHeader;
  }

  private void setDefaultHeaders(RestfulStepEntity entity, Map<String, String> headers) {
    log.debug("Setting default headers for entity:" + entity);
    log.debug("Set default headers, headers:" + headers);
    try {
      headers = (headers != null) ? headers : new HashMap<String, String>();

      if (headers.get("Host") == null) {
        URL url = new URL(entity.getUrl());
        headers.put("Host", url.getHost());
      }

      if (headers.get("Content-Type") == null) {
        headers.put("Content-Type", "application/json");
      }
    } catch (Exception e) {
      log.error("error while setting default headers");
    }

  }


  private HttpEntity getEntity(RestfulStepEntity entity, Map<String, String> envSettings, Map<String, String> headers) throws Exception {

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    if (entity.getIsMultipart() != null && entity.getIsMultipart()) {
      removeContentTypeHeader(headers);
      Map<String, Object> payload = new ObjectMapperService().parseJson(entity.getPayload(), new TypeReference<>() {
      });
      for (Map.Entry<String, Object> data : payload.entrySet()) {
        removeContentTypeHeader(headers);
        boolean isFileUrl = data.getValue() != null && (data.getValue().toString().startsWith("http")
          || data.getValue().toString().startsWith("https"));
        if (isFileUrl) {
          String filePath = downloadFile(data.getValue().toString(), envSettings);
          String[] fileNames = filePath.split(File.separator);
          builder.addBinaryBody(data.getKey(), new File(filePath), ContentType.DEFAULT_BINARY, fileNames[fileNames.length - 1]);
        } else {
          builder.addPart(data.getKey(), new StringBody(new ObjectMapperService().convertToJson(data.getValue()), ContentType.APPLICATION_JSON));
        }
      }
      return builder.build();
    } else if (entity.getPayload() != null) {
      return new StringEntity(entity.getPayload());
    }
    return null;
  }

  public void removeContentTypeHeader(Map<String, String> headers) {
    String contenttype = null;
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("content-type")) {
        contenttype = entry.getKey();
        break;
      }
    }
    if (contenttype != null) {
      headers.remove(contenttype);
    }
  }


  public HttpResponse<String> executeRestCall(String url, RequestMethod method, Map<String, String> headers,
                                              HttpEntity input) throws IOException {
    HttpResponse<String> returnResponse;
    log.debug(String.format("Executing Rest API call with below props\n method::%s\n, headers::%s\n HttpEntity::%s", method,
      headers, input));
    try {
      HttpUriRequest request = null;
      switch (method) {
        case GET:
          request = new HttpGet(url);
          setHeaders(request, headers);

          break;
        case POST:
          request = new HttpPost(url);
          if (input != null)
            ((HttpPost) request).setEntity(input);
          setHeaders(request, headers);
          break;
        case PUT:
          request = new HttpPut(url);
          if (input != null)
            ((HttpPut) request).setEntity(input);
          setHeaders(request, headers);
          break;
        case PATCH:
          request = new HttpPatch(url);
          if (input != null)
            ((HttpPatch) request).setEntity(input);
          setHeaders(request, headers);
          break;
        case DELETE:
          request = new HttpDelete(url);
          setHeaders(request, headers);
          break;
        case OPTIONS:
          request = new HttpOptions(url);
          setHeaders(request, headers);
          break;
        case TRACE:
          request = new HttpTrace(url);
          setHeaders(request, headers);
          break;
        case HEAD:
          request = new HttpHead(url);
          setHeaders(request, headers);
          break;
        default:
          break;
      }
      log.debug("***Executing REST API Call***");
      org.apache.http.HttpResponse response = httpclient.execute(request);

      log.debug("URL : " + url);
      log.debug("Response Status Code : " + response.getStatusLine().getStatusCode());
      returnResponse = new HttpResponse<>(response, new TypeReference<>() {
      });
      return returnResponse;
    } catch (Exception e) {
      throw e;
    } finally {
      HttpClientUtils.closeQuietly(httpclient);
    }
  }

  private void setHeaders(HttpUriRequest request, Map<String, String> headers) {
    if (headers != null) {
      for (String key : headers.keySet()) {
        request.addHeader(key, headers.get(key));
      }
    }
  }

  private String downloadFile(String fileUrl, Map<String, String> envSettings) throws Exception {
    try {
      if (fileUrl.startsWith(HTTP) || fileUrl.startsWith(HTTPS)) {
        String fileName = FilenameUtils.getName(new java.net.URL(fileUrl).getPath());
        String filePath = getDownloadFilePath(envSettings);
        com.testsigma.automator.http.HttpClient httpClient = EnvironmentRunner.getAssetsHttpClient();
        HttpResponse<String> response = httpClient.downloadFile(fileUrl, filePath + File.separator + fileName);

        if (response != null && response.getStatusCode() == HttpStatus.OK.value()) {
          return filePath + File.separator + fileName;
        } else {
          throw new TestsigmaFileNotFoundException(AutomatorMessages.getMessage(AutomatorMessages.EXCEPTION_DOWNLOAD_LOCAL_FILE, fileUrl));
        }
      } else {
        return fileUrl;
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  private String getDownloadFilePath(Map<String, String> envSettings) {

    String fullPath = Paths.get(PathUtil.getInstance().getUploadPath(), envSettings.get("envRunId"))
      .toFile().getAbsolutePath();

    File file = new File(fullPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    return fullPath;
  }
}
