package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponseMetadataRequest {

  private int status;
  private Map<String, String> headers; //{"match_type":"FULL", "data":{}}
  private String content;  //{"match_type":"FULL", "data":{}}
}
