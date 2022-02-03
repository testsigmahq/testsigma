package com.testsigma.dto;

import lombok.Data;

@Data
public class RestStepEntityDTO {
  private Long id;
  private Long stepId;
  private String url;
  private String method;
  private String requestHeaders;
  private String payload;
  private String status;
  private String headerCompareType;
  private String responseHeaders;
  private String responseCompareType;
  private String response;
  private String expectedResultType;
  private String headerRuntimeData;
  private String bodyRuntimeData;
  private Boolean followRedirects;
  private Integer authorizationType;
  private String authorizationValue;
  private Boolean isMultipart;
  private Boolean storeMetadata;
}
