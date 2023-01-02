package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.*;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepCloudDataMap {

  @JsonProperty("condition_if")
  private Object ifConditionExpectedResults;
  @JsonProperty("condition-type")
  private TestStepConditionType conditionType;
  @JsonProperty("test-data")
  private Map<String, TestStepNlpData> testData;
  @JsonProperty("kibbutz_test_data_function")
  private AddonTestStepTestData kibbutzTDF;
  @JsonProperty("test-data-function")
  private CloudTestDataFunction testDataFunction;
  @JsonProperty("custom-step")
  private TestStepCustomStep customStep;
  @JsonProperty("ui-identifier")
  private String element;
  @JsonProperty("from-ui-identifier")
  private String fromElement;
  @JsonProperty("to-ui-identifier")
  private String toElement;
  @JsonProperty("attribute")
  private String attribute;
  @JsonProperty("for_loop")
  private TestStepCloudForLoop forLoop;
  @JsonProperty("while_loop")
  private TestStepWhileLoop whileLoop;
  @JsonProperty("whileCondition")
  private String whileCondition;
}
