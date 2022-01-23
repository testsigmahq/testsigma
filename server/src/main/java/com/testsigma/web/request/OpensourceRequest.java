package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpensourceRequest {
  private String url;
  @JsonProperty("user_name")
  private String userName;
  @JsonProperty("access_key")
  private String accessKey;
}
