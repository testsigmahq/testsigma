package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.automator.constants.TestStepConditionType;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepDataMapEntity {

  private Object ifConditionExpectedResults;
  private TestStepConditionType conditionType;
  private Map<String, TestStepNlpDataEntity> testData;
  private String element;
  private String fromElement;
  private String toElement;
  private String attribute;
  private TestStepForLoopEntity forLoop;
}
