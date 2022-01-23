package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseDetails {

  @JsonProperty("name")
  private String name;
  @JsonProperty("requirement_id")
  private Long requirementId;

  @JsonProperty("testData")
  private String testData;
  @JsonProperty("testDataSetName")
  private String testDataSetName;
  @JsonProperty("prerequisite")
  private Long prerequisite;


}
