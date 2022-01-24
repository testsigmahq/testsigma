/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.constants.AgentOs;
import com.testsigma.agent.dto.AgentDTO;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.init.WrapperConnector;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.android.AndroidDeviceListener;
import com.testsigma.agent.mobile.ios.IosDeviceListener;
import com.testsigma.agent.services.AgentService;
import com.testsigma.agent.utils.NetworkUtil;
import com.testsigma.agent.ws.server.AgentWebServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(path = "/api/v1/agent")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgentsController {
  private final AgentConfig agentConfig;
  private final AndroidDeviceListener androidDeviceListener;
  private final IosDeviceListener iosDeviceListener;
  private final DeviceContainer deviceContainer;
  private final AgentWebServer agentWebServer;

  @GetMapping(value = "/status")
  public ResponseEntity<String> status() {
    log.info("Processing request /api/v1/agent/status");
    return new ResponseEntity<>(agentConfig.getRegistered().toString(), HttpStatus.OK);
  }

  @GetMapping(value = "/agent_info")
  public ResponseEntity<AgentDTO> getAgentInfo() {
    log.info("Processing request /api/v1/agent/agent_info");
    AgentDTO agentDTO = new AgentDTO();
    agentDTO.setHostName(AgentService.getComputerName());
    agentDTO.setOsType(AgentOs.getLocalAgentOs());
    agentDTO.setOsVersion(AgentService.getOsVersion());
    agentDTO.setAgentVersion(this.agentConfig.getAgentVersion());
    agentDTO.setIsRegistered(this.agentConfig.getRegistered());
    agentDTO.setUniqueId(this.agentConfig.getUUID());
    agentDTO.setIpAddress(NetworkUtil.getCurrentIpAddress());
    return new ResponseEntity<>(agentDTO, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public HttpStatus deregisterAgent(@PathVariable("uuid") String uuid) {
    log.info("Received request for deleting agent with UUID - " + uuid);
    try {
      if (uuid.equals(this.agentConfig.getUUID())) {
        log.info("Removing agent config details");
        try {
          androidDeviceListener.removeDeviceListenerCallback();
          iosDeviceListener.removeDeviceListenerCallback();
          deviceContainer.disconnectDevices();
          agentWebServer.stopWebServerConnectors();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
        agentConfig.setRegistered("false");
        agentConfig.setJwtApiKey(null);
        agentConfig.setUUID(null);
        agentConfig.removeConfig();
        return HttpStatus.OK;
      } else {
        log.warn("No matching agent with the UUID found...");
      }
    } catch (TestsigmaException e) {
      log.error(e.getMessage(), e);
    } finally {
      WrapperConnector.getInstance().shutdown();
    }
    return HttpStatus.BAD_REQUEST;
  }
}
