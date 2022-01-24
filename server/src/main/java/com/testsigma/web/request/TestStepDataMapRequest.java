package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepConditionType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepDataMapRequest {

  private ResultConstant[] ifConditionExpectedResults;
  private TestStepConditionType conditionType;
  private String testData;
  private String testDataType;
  private String element;
  private String fromElement;
  private String toElement;
  private String attribute;
  private TestStepForLoopRequest forLoop;
}
