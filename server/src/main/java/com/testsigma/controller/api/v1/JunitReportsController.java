/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.v1;

import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.JUNITTestSuitesNodeDTO;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestPlanResult;
import com.testsigma.service.JunitReportService;
import com.testsigma.service.TestPlanResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "apiJunitReportsController")
@Log4j2
@RequestMapping(path = "/api/v1/reports/junit")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class JunitReportsController {
  private final TestPlanResultService testPlanResultService;
  private final JunitReportService junitReportService;

  @RequestMapping(value = {"/{testPlanResultId}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE})
  public String getReport(@PathVariable(value = "testPlanResultId") Long testPlanResultId) throws Exception {
    TestPlanResult testPlanResult = testPlanResultService.find(testPlanResultId);
    if (testPlanResult.getStatus() != StatusConstant.STATUS_COMPLETED) {
      return MessageConstants.REPORT_GENERATION_FAILED_TEST_PLAN_RUN_IS_NOT_COMPLETED;
    }
    JUNITTestSuitesNodeDTO JUNITTestSuitesNodeDTO = junitReportService.generateJunitReport(testPlanResult.getTestPlanId(),
      testPlanResultId);
    return junitReportService.getFormattedXML(JUNITTestSuitesNodeDTO);
  }

}
