package com.testsigma.web.request;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseCopyRequest {
  Boolean isStepGroup;
  private String name;
  private Long testCaseId;
  private List<Long> stepIds;
}
