package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReRunType {
  NONE, ALL_TESTS, ONLY_FAILED_TESTS;

  public static Boolean runFailedTestCases(ReRunType reRunType){
    // ONLY_FAILED_TESTS Type added to support the old executions
    return (reRunType == NONE || reRunType == ONLY_FAILED_TESTS || reRunType == ALL_TESTS);
  }
}
