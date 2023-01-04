package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepNlpData;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepDataMapRequest {

  private ResultConstant[] ifConditionExpectedResults;
  private TestStepConditionType conditionType;
  private Map<String, TeststepNlpDataRequest> testData;
  private String element;
  private String fromElement;
  private String toElement;
  private String attribute;
  private TestStepForLoopRequest forLoop;
}
