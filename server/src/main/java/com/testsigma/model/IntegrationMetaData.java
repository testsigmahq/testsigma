package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IntegrationMetaData {
  String channel;
  @JsonProperty("user_name")
  String userName;
}
