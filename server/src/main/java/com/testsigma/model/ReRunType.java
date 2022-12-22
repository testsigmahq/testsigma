package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReRunType {
  NONE, ALL_TESTS, ONLY_FAILED_TESTS, ALL_ITERATIONS, ONLY_FAILED_ITERATIONS, ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS;

  public static Boolean runFailedIterations(ReRunType reRunType){
    return (reRunType == ONLY_FAILED_ITERATIONS || reRunType == ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS);
  }

  public static Boolean runFailedTestCases(ReRunType reRunType){
    // ONLY_FAILED_TESTS Type added to support the old executions
    return (reRunType == ALL_ITERATIONS || reRunType == ONLY_FAILED_ITERATIONS ||
            reRunType == ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS || reRunType == ONLY_FAILED_TESTS);
  }

  public static Boolean runAllIterations(ReRunType reRunType){
    // ONLY_FAILED_TESTS Type added to support the old executions
    return (reRunType == ALL_TESTS || reRunType == ALL_ITERATIONS || reRunType == ONLY_FAILED_TESTS);

  }
}
