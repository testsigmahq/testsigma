/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.agent;

import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController(value = "agentAPIsTestCaseController")
@RequestMapping(path = "/api/agents/test_case/{id}")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCasesController {

  private final TestCaseService testCaseService;


  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public TestCaseEntityDTO find(@PathVariable("id") Long id,
                                @RequestParam(value = "testDataSetName", required = false) String testDataSetName,
                                @RequestParam("testCaseResultId") Long testCaseResultId,
                                @RequestParam("environmentResultId") Long environmentResultId
  ) throws Exception {
    return testCaseService.find(id, environmentResultId, testDataSetName, testCaseResultId);
  }
}
