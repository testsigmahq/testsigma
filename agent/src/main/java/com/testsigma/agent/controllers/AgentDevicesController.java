/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.dto.AgentDeviceDTO;
import com.testsigma.agent.exception.DeviceNotConnectedException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.mappers.MobileDeviceMapper;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.MobileDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(path = "/api/v1/agent_devices/{unique_id}")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgentDevicesController {
  private final DeviceContainer deviceContainer;
  private final MobileDeviceMapper mobileDeviceMapper;

  /**
   * fetch the device details using device unique id.
   *
   * @param uniqueId
   * @return AgentDeviceDTO - connected agent device details
   * @throws DeviceNotConnectedException
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AgentDeviceDTO show(@PathVariable("unique_id") String uniqueId)
    throws DeviceNotConnectedException, TestsigmaException {
    log.info("Received request fetch device details - " + uniqueId);
    MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
    if (mobileDevice == null) {
      throw new DeviceNotConnectedException("Device not online. Please check if the device is connected properly.");
    }
    return mobileDeviceMapper.map(mobileDevice);
  }
}
