/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.exception.MobileAutomationServerCommandExecutionException;
import com.testsigma.agent.mobile.DeviceCommand;
import com.testsigma.agent.request.TapPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(path = "/api/v1/device_actions/{unique_id}")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgentDeviceActionsController {

  private final DeviceCommand deviceCommand;

  /**
   * Tap on the current page at the given X, Y point
   *
   * @param uniqueId
   * @param tapPoint
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/tap")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void tap(@PathVariable("unique_id") String uniqueId, @RequestBody TapPoint tapPoint)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Received a request for a Tap on device - " + uniqueId + " at (" + tapPoint.getX() +
      "," + tapPoint.getY() + ")");
    deviceCommand.tap(uniqueId, tapPoint);
  }

  /**
   * swipe on the current page from point X1, Y1 to point X2, Y2
   *
   * @param uniqueId
   * @param tapPoints
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/swipe")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void swipe(@PathVariable("unique_id") String uniqueId, @RequestBody TapPoint[] tapPoints)
    throws MobileAutomationServerCommandExecutionException {

    TapPoint fromTapPoint = tapPoints[0];
    TapPoint toTapPoint = tapPoints[1];
    log.info(
      "Received a request for a swipe operation on device " + uniqueId + " from (" + fromTapPoint
        .getX() +
        "," + fromTapPoint.getY() + ") to (" + toTapPoint.getX() + "," + toTapPoint.getY() + ")");
    deviceCommand.swipe(uniqueId, tapPoints);
  }

  /**
   * navigate to the previous page from the current page in the device
   *
   * @param uniqueId
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @GetMapping(value = "/navigate/back")
  @ResponseStatus(HttpStatus.OK)
  public void navigateBack(@PathVariable("unique_id") String uniqueId)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Navigating to back for device - " + uniqueId);
    deviceCommand.back(uniqueId);
  }
}
