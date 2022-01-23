package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.model.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value = "test-step-data")
public class TestStepDataMapXMLDTO extends BaseXMLDTO {

  @JsonProperty("condition_if")
  private Object ifConditionExpectedResults;
  @JsonProperty("condition-type")
  private TestStepConditionType conditionType;
  @JsonProperty("test-data")
  private String testData;
  @JsonProperty("test-data-function")
  private DefaultDataGenerator defaultDataGenerator;
  @JsonProperty("custom-step")
  private TestStepCustomStep customStep;
  @JsonProperty("test-data-type")
  private String testDataType;
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
}
