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
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.converter.JSONObjectConverter;
import com.testsigma.model.HttpRequestMethod;
import com.testsigma.model.RestStepAuthorizationType;
import com.testsigma.model.RestStepCompareType;
import lombok.Data;
import org.json.JSONObject;

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
  @JsonProperty("Payload")
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
  @JsonProperty("StoreMetadata")
  private Boolean storeMetadata;
  @JsonProperty("Response")
  private String response;
  @JsonProperty("HeaderRuntimeData")
  private String headerRuntimeData;
  @JsonProperty("BodyRuntimeData")
  private String bodyRuntimeData;
  @JsonProperty("FollowRedirects")
  private Boolean followRedirects;
  @JsonProperty("AuthorizationType")
  private RestStepAuthorizationType authorizationType;
  @JsonProperty("AuthorizationValue")
  private String authorizationValue;
  @JsonProperty("IsMultipart")
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
