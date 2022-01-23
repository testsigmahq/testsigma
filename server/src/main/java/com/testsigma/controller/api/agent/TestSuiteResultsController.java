/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.agent;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestSuiteResultMapper;
import com.testsigma.service.TestSuiteResultService;
import com.testsigma.web.request.TestSuiteResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(value = "agentTestSuiteResultsController")
@RequestMapping(path = "/api/agents/test_suite_results/{id}")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class TestSuiteResultsController {

  private final TestSuiteResultService testSuiteResultService;
  private final TestSuiteResultMapper testSuiteResultMapper;

  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity<String> update(@PathVariable("id") Long id,
                                       @RequestBody TestSuiteResultRequest testSuiteResultRequest)
    throws TestsigmaException {
    testSuiteResultService.updateResult(testSuiteResultRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path = "/result", method = RequestMethod.PUT)
  public ResponseEntity<String> updateResult(@PathVariable("id") Long id,
                                             @RequestBody TestSuiteResultRequest testCaseGroupResultRequest)
    throws TestsigmaDatabaseException, ResourceNotFoundException {
    testSuiteResultService.updateResultData(testCaseGroupResultRequest);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}
