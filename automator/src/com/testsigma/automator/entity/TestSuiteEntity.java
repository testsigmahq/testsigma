package com.testsigma.automator.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestSuiteEntity {
  private Long id;
  private String name;
  private Long resultId;
  private Long preRequisite;
  private Long environmentResultId;
  private Long testPlanResultId;
  private List<TestCaseEntity> testCases = new ArrayList<>();
  private String videoPreSignedURL;
  private String seleniumLogPreSignedURL;
  private String consoleLogPreSignedURL;
  private String harLogPreSignedURL;
  private String appiumLogPreSignedURL;
}
