/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.JUNITTestSuitesNodeDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.TestPlanResult;
import com.testsigma.model.StatusConstant;
import com.testsigma.service.JunitReportService;
import com.testsigma.service.TestPlanResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;

@RestController
@Log4j2
@RequestMapping(path = "/reports/junit")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class JunitReportsController {
    private final TestPlanResultService executionResultService;
    private final JunitReportService junitReportService;

    @RequestMapping(value = {"/{testPlanResultId}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE})
    public String getReport(@PathVariable(value = "testPlanResultId") Long testPlanResultId) throws Exception {
        TestPlanResult testPlanResult = executionResultService.find(testPlanResultId);
        if (!StatusConstant.STATUS_COMPLETED.equals(testPlanResult.getStatus())) {
            throw new ResourceNotFoundException(HttpStatus.UNAUTHORIZED.getReasonPhrase() +
                    MessageConstants.MSG_REPORT_GENERATION_FAILED_EXECUTION_IN_PROGRESS);
        }
        JUNITTestSuitesNodeDTO JUNITTestSuitesNodeDTO = junitReportService.generateJunitReport(testPlanResult.getTestPlanId(),
                testPlanResultId);
        return junitReportService.getFormattedXML(JUNITTestSuitesNodeDTO);
    }

}
