package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestRequestMetadata {

  private Long id;
  private String url;
  private String method;
  private String requestHeaders;
  private String payload;
  private String status;
  private String headerCompareType;
  private String responseHeaders;
  private String responseCompareType;
  private String response;
  private Long stepId;
  private String expectedResultType;
  private Boolean storeMetadata;
  private String headerRuntimeData;
  private String bodyRuntimeData;
  private Boolean followRedirects;
  private AuthorizationTypes authorizationType;
  private String authorizationValue;
  private Boolean isMultipart;
}
