package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepDataMap {


  @JsonProperty("condition_if")
  private Object ifConditionExpectedResults;
  @JsonProperty("condition-type")
  private TestStepConditionType conditionType;
  @JsonProperty("test-data")
  private Map<String, TestStepNlpData> testData;
  @JsonProperty("custom-step")
  private TestStepCustomStep customStep;
  @JsonProperty("element")
  private String element;
  @JsonProperty("from-element")
  private String fromElement;
  @JsonProperty("to-element")
  private String toElement;
  @JsonProperty("attribute")
  private String attribute;
  @JsonProperty("for_loop")
  private TestStepForLoop forLoop;
  @JsonProperty("while_loop")
  private TestStepWhileLoop whileLoop;
  @JsonProperty("whileCondition")
  private String whileCondition;
  @JsonProperty("visual_enabled")
  private Boolean visualEnabled;
  @JsonProperty("migrated")
  private Boolean migrated = Boolean.FALSE;
}
