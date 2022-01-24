/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.dto.DriverSessionDTO;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.request.DriverSessionRequest;
import com.testsigma.agent.services.AgentService;
import com.testsigma.agent.services.DriverSessionsService;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping(path = "/api/v1/device_sessions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverSessionsController {

  private final DriverSessionsService driverSessionsService;

  /**
   * Create a remote web driver session using selenium/appium
   *
   * @param driverSessionRequest
   * @return created remote web driver sessionId
   * @throws Exception
   */
  @PostMapping
  @ResponseBody
  public DriverSessionDTO createSession(@RequestBody DriverSessionRequest driverSessionRequest)
    throws Exception {
    log.debug("Creating a remote web driver session for - " + driverSessionRequest.getUniqueId() + " ( " + driverSessionRequest + ") ");
    if (driverSessionRequest.getMobileSessionId() != null) {
      try {
        String sessionId = driverSessionsService.createSession(driverSessionRequest);

        DriverSessionDTO driverSessionDTO = new DriverSessionDTO();
        driverSessionDTO.setSessionId(sessionId);
        driverSessionDTO.setHostname(AgentService.getComputerName());
        return driverSessionDTO;
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new AutomatorException(e.getMessage(), e);
      }
    } else {
      throw new TestsigmaException("Failed creating driver session: mobileSessionId is NULL");
    }
  }

  /**
   * get the status of a remote web driver session
   *
   * @param sessionId
   * @return session status
   * @throws Exception
   */
  @GetMapping(value = "/{sessionId}")
  public DriverSessionDTO getSession(@PathVariable("sessionId") String sessionId)
    throws Exception {
    log.debug("Getting remote web driver session details with session Id- " + sessionId);
    String sessionStatus = driverSessionsService.getSession(sessionId);
    DriverSessionDTO driverSessionDTO = new DriverSessionDTO();
    driverSessionDTO.setSessionId(sessionId);
    driverSessionDTO.setStatus(sessionStatus);
    return driverSessionDTO;
  }

  /**
   * delete and existing remote web driver session.
   *
   * @param sessionId
   * @return no application specific return value. Only corresponding http status codes.
   * @throws Exception
   */
  @DeleteMapping(value = "/{sessionId}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteSession(@PathVariable("sessionId") String sessionId)
    throws Exception {
    log.debug("Deleting a remote web driver session with session Id- " + sessionId);
    driverSessionsService.deleteSession(sessionId);
  }

}
