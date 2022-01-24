package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.automator.constants.TestStepConditionType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepDataMapEntity {

  private Object ifConditionExpectedResults;
  private TestStepConditionType conditionType;
  private String testData;
  private DefaultDataGeneratorsEntity testDataFunction;
  private String testDataType;
  private String element;
  private String fromElement;
  private String toElement;
  private String attribute;
  private TestStepForLoopEntity forLoop;
}
