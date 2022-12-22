/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.converter.JSONObjectConverter;
import com.testsigma.model.HttpRequestMethod;
import com.testsigma.model.RestStepAuthorizationType;
import com.testsigma.model.RestStepCompareType;
import lombok.Data;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonRootName(value = "RestStep")
@JsonListRootName(name = "RestSteps")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestStepCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("StepId")
  private Long stepId;
  @JsonProperty("Url")
  private String url;
  @JsonProperty("Method")
  private HttpRequestMethod method;
  @JsonProperty("RequestHeaders")
  private String requestHeaders;
  @JacksonXmlElementWrapper(localName = "RequestHeadersMap")
  @JacksonXmlProperty(localName = "RequestHeaderEntry")
  private List<Entry> requestHeadersMap;
  @JsonProperty("Payload")
  @JacksonXmlCData
  private String payload;
  @JsonProperty("ExpectedResponseStatus")
  private String expectedResponseStatus;
  @JsonProperty("ExpectedResultType")
  private String expectedResultType;
  @JsonProperty("ResponseCompareType")
  private RestStepCompareType responseCompareType;
  @JsonProperty("HeaderCompareType")
  private RestStepCompareType headerCompareType = RestStepCompareType.LENIENT;
  @JsonProperty("ResponseHeaders")
  private String responseHeaders;
  @JacksonXmlElementWrapper(localName = "ResponseHeadersMap")
  @JacksonXmlProperty(localName = "ResponseHeaderEntry")
  private List<Entry> responseHeadersMap;
  @JsonProperty("StoreMetadata")
  private Boolean storeMetadata;
  @JsonProperty("Response")
  @JacksonXmlCData
  private String response;
  @JsonProperty("HeaderRuntimeData")
  private String headerRuntimeData;
  @JacksonXmlElementWrapper(localName = "HeaderRuntimeDataMap")
  @JacksonXmlProperty(localName = "HeaderRuntimeDataEntry")
  private List<Entry> headerRuntimeDataMap;
  @JsonProperty("BodyRuntimeData")
  private String bodyRuntimeData;
  @JacksonXmlElementWrapper(localName = "BodyRuntimeDataMap")
  @JacksonXmlProperty(localName = "BodyRuntimeDataEntry")
  private List<Entry> bodyRuntimeDataMap;
  @JsonProperty("FollowRedirects")
  private Boolean followRedirects;
  @JsonProperty("AuthorizationType")
  private RestStepAuthorizationType authorizationType;
  @JsonProperty("AuthorizationValue")
  private String authorizationValue;
  @JacksonXmlElementWrapper(localName = "AuthorizationValueMap")
  @JacksonXmlProperty(localName = "AuthorizationValueEntry")
  private List<Entry> authorizationValueMap;
  @JsonProperty("IsMultipart")
  private Boolean isMultipart;

  public void setAuthorizationValueMap(List<Entry> authorizationValueMap) {
    this.authorizationValueMap = authorizationValueMap;
  }

  public JSONObject getAuthorizationValue() {
    if(this.authorizationValueMap == null) {
      return null;
    }
    Map<String, Object> map = new HashMap<>();
    this.authorizationValueMap.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
    return new JSONObject(map);
  }

  public void setAuthorizationValue(JSONObject authorizationValue) {
    this.authorizationValue = new JSONObjectConverter().convertToDatabaseColumn(authorizationValue);
  }

  public void setRequestHeadersMap(List<Entry> requestHeadersMap) {
    this.requestHeadersMap = requestHeadersMap;
  }

  public JSONObject getRequestHeaders() {
    if(this.requestHeadersMap == null) {
      return new JSONObject();
    }
    Map<String, Object> map = new HashMap<>();
    this.requestHeadersMap.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
    return new JSONObject(map);
  }

  public void setRequestHeaders(JSONObject requestHeaders) {
    this.requestHeaders = new JSONObjectConverter().convertToDatabaseColumn(requestHeaders);
  }

  public void setResponseHeadersMap(List<Entry> responseHeadersMap) {
    this.responseHeadersMap = responseHeadersMap;
  }

  public JSONObject getResponseHeaders() {
    if(this.responseHeadersMap == null) {
      return new JSONObject();
    }
    Map<String, Object> map = new HashMap<>();
    this.responseHeadersMap.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
    return new JSONObject(map);
  }

  public void setResponseHeaders(JSONObject responseHeaders) {
    this.responseHeaders = new JSONObjectConverter().convertToDatabaseColumn(responseHeaders);
  }

  public void setHeaderRuntimeDataMap(List<Entry> headerRuntimeDataMap) {
    this.headerRuntimeDataMap = headerRuntimeDataMap;
  }

  public JSONObject getHeaderRuntimeData() {
    if(this.headerRuntimeDataMap == null) {
      return null;
    }
    Map<String, Object> map = new HashMap<>();
    this.headerRuntimeDataMap.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
    return new JSONObject(map);
  }

  public void setHeaderRuntimeData(JSONObject headerRuntimeData) {
    this.headerRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(headerRuntimeData);
  }

  public void setBodyRuntimeDataMap(List<Entry> bodyRuntimeDataMap) {
    this.bodyRuntimeDataMap = bodyRuntimeDataMap;
  }

  public JSONObject getBodyRuntimeData() {
    if(this.bodyRuntimeDataMap == null) {
      return null;
    }
    Map<String, Object> map = new HashMap<>();
    this.bodyRuntimeDataMap.forEach((entry) -> map.put(entry.getKey(), entry.getValue()));
    return new JSONObject(map);
  }

  public void setBodyRuntimeData(JSONObject bodyRuntimeData) {
    this.bodyRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(bodyRuntimeData);
  }
}
