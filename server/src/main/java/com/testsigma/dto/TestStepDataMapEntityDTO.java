package com.testsigma.dto;

import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepNlpData;
import lombok.Data;

import java.util.Map;

@Data
public class TestStepDataMapEntityDTO {
  private Object ifConditionExpectedResults;
  private TestStepConditionType conditionType;
  private Map<String, TestStepNlpDataEntityDTO> testDataMap;
  private DefaultDataGeneratorsEntity testDataFunction;
  private String testDataType;
  private String element;
  private String fromElement;
  private String toElement;
  private String attribute;
  private TestStepForLoopEntityDTO forLoop;
}
