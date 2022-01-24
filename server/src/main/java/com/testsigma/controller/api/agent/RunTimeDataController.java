/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller.api.agent;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.service.RunTimeDataService;
import com.testsigma.web.request.RuntimeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController(value = "agentRunTimeDataController")
@RequestMapping(path = "/api/agents/run_time_data")
public class RunTimeDataController {
  private final RunTimeDataService runTimeDataService;

  @RequestMapping(value = "/{variableName}", method = RequestMethod.GET)
  public String getRunTimeData(@PathVariable("variableName") String variableName, @RequestParam(value = "environmentResultId") Long environmentResultId,
                               @RequestParam(value = "sessionId") String sessionId)
    throws ResourceNotFoundException {

    return runTimeDataService.getRunTimeData(variableName, environmentResultId, sessionId);
  }

  @RequestMapping(value = "/variable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  public void saveRunTimeData(@RequestParam(value = "environmentResultId") Long environmentResultId,
                              @RequestBody RuntimeRequest runtimeRequest) throws ResourceNotFoundException {
    runTimeDataService.updateRunTimeData(environmentResultId, runtimeRequest);
  }
}
