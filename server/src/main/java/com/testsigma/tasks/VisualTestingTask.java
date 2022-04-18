/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.tasks;

import com.testsigma.model.TestCaseResult;
import com.testsigma.service.VisualTestingService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class VisualTestingTask extends Thread {
  private final TestCaseResult testCaseResult;
  private final VisualTestingService visualTestingService;

  public VisualTestingTask(TestCaseResult testCaseResult, VisualTestingService visualTestingService) {
    this.testCaseResult = testCaseResult;
    this.visualTestingService = visualTestingService;
  }

  @Override
  public void run() {
    try {
      this.visualTestingService.initScreenshotComparison(testCaseResult);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      log.error("Exception while handling ImageComparison for testCaseResult:" + testCaseResult, e);
    }
  }
}
