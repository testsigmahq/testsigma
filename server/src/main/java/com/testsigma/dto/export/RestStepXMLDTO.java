/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.converter.JSONObjectConverter;
import com.testsigma.model.HttpRequestMethod;
import com.testsigma.model.RestStepAuthorizationType;
import com.testsigma.model.RestStepCompareType;
import lombok.Data;
import org.json.JSONObject;

@Data
@JsonRootName(value = "rest-step")
@JsonListRootName(name = "rest-steps")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestStepXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("step-id")
  private Long stepId;
  @JsonProperty("url")
  private String url;
  @JsonProperty("method")
  private HttpRequestMethod method;
  @JsonProperty("request-headers")
  private String requestHeaders;
  @JsonProperty("payload")
  private String payload;
  @JsonProperty("expected-response-status")
  private String expectedResponseStatus;
  @JsonProperty("expected-result-type")
  private String expectedResultType;
  @JsonProperty("response-compare-type")
  private RestStepCompareType responseCompareType;
  @JsonProperty("header-compare-type")
  private RestStepCompareType headerCompareType = RestStepCompareType.LENIENT;
  @JsonProperty("response-headers")
  private String responseHeaders;
  @JsonProperty("store-metadata")
  private Boolean storeMetadata;
  @JsonProperty("response")
  private String response;
  @JsonProperty("header-runtime-data")
  private String headerRuntimeData;
  @JsonProperty("body-runtime-data")
  private String bodyRuntimeData;
  @JsonProperty("follow-redirects")
  private Boolean followRedirects;
  @JsonProperty("authorization-type")
  private RestStepAuthorizationType authorizationType;
  @JsonProperty("authorization-value")
  private String authorizationValue;
  @JsonProperty("is-multipart")
  private Boolean isMultipart;

  public JSONObject getAuthorizationValue() {
    return new JSONObjectConverter().convertToEntityAttribute(this.authorizationValue);
  }

  public void setAuthorizationValue(JSONObject authorizationValue) {
    this.authorizationValue = new JSONObjectConverter().convertToDatabaseColumn(authorizationValue);
  }

  public JSONObject getRequestHeaders() {
    return new JSONObjectConverter().convertToEntityAttribute(this.requestHeaders);
  }

  public void setRequestHeaders(JSONObject requestHeaders) {
    this.requestHeaders = new JSONObjectConverter().convertToDatabaseColumn(requestHeaders);
  }

  public JSONObject getResponseHeaders() {
    return new JSONObjectConverter().convertToEntityAttribute(this.responseHeaders);
  }

  public void setResponseHeaders(JSONObject responseHeaders) {
    this.responseHeaders = new JSONObjectConverter().convertToDatabaseColumn(responseHeaders);
  }

  public JSONObject getHeaderRuntimeData() {
    return new JSONObjectConverter().convertToEntityAttribute(this.headerRuntimeData);
  }

  public void setHeaderRuntimeData(JSONObject headerRuntimeData) {
    this.headerRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(headerRuntimeData);
  }

  public JSONObject getBodyRuntimeData() {
    return new JSONObjectConverter().convertToEntityAttribute(this.bodyRuntimeData);
  }

  public void setBodyRuntimeData(JSONObject bodyRuntimeData) {
    this.bodyRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(bodyRuntimeData);
  }

}
