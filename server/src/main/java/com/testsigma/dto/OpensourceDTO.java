package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpensourceDTO {
  private String url;
  @JsonProperty("user_name")
  private String userName;
  @JsonProperty("access_key")
  private String accessKey;
}
