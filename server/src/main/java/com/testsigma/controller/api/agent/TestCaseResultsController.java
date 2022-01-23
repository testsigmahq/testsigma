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
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestCaseResultMapper;
import com.testsigma.service.TestCaseResultService;
import com.testsigma.web.request.TestCaseResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController(value = "agentTestCaseResultsController")
@RequestMapping(path = "/api/agents/test_case_results/{id}")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestCaseResultsController {

  private final TestCaseResultService testCaseResultService;
  private final TestCaseResultMapper testCaseResultMapper;

  @RequestMapping(method = RequestMethod.PUT, consumes = "application/json;charset=UTF-8")
  public ResponseEntity<String> update(@PathVariable("id") Long id,
                                       @RequestBody TestCaseResultRequest testCaseResultRequest)
    throws UnsupportedEncodingException, TestsigmaException {
    testCaseResultService.updateResult(testCaseResultRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(path = "/result", method = RequestMethod.PUT)
  public ResponseEntity<String> updateResult(@PathVariable("id") Long id,
                                             @RequestBody TestCaseResultRequest testCaseResultRequest)
    throws ResourceNotFoundException {
    testCaseResultService.updateResultData(testCaseResultRequest);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}
