/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.AgentDeviceDTO;
import com.testsigma.mapper.AgentDeviceMapper;
import com.testsigma.model.AgentDevice;
import com.testsigma.service.AgentDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = {"/settings/agent_devices"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgentDevicesController {

  private final AgentDeviceService agentDeviceService;
  private final AgentDeviceMapper agentDeviceMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<AgentDeviceDTO> index(
    Pageable pageable,
    @RequestParam(value = "agentId", required = false) Long agentId,
    @RequestParam(value = "available", required = false) Boolean available,
    @RequestParam(value = "provisioned", required = false) Boolean provisioned) {

    log.info(String.format("Received a GET request /settings/agent_devices with additional data. " +
      "agent id [%s] available [%s] provisioned [%s]", agentId, available, provisioned));

    Page<AgentDevice> agentDevices;

    if ((available != null) && available) {
      agentDevices = agentDeviceService.findAllByAgentIdAndIsOnline(agentId, pageable);
      List<AgentDeviceDTO> agentDeviceDTOs = agentDeviceMapper.map(agentDevices.getContent());
      return new PageImpl<>(agentDeviceDTOs, pageable, agentDevices.getTotalElements());
    } else if ((provisioned != null) && provisioned) {
      agentDevices = agentDeviceService.findAllByAgentId(agentId, pageable);
      List<AgentDeviceDTO> agentDeviceDTOs = agentDeviceMapper.map(agentDevices.getContent());
      agentDeviceService.setProvisionedFlag(agentDeviceDTOs);
      return new PageImpl<>(agentDeviceDTOs, pageable, agentDevices.getTotalElements());
    } else if (agentId != null) {
      agentDevices = agentDeviceService.findAllByAgentId(agentId, pageable);
      List<AgentDeviceDTO> agentDeviceDTOs = agentDeviceMapper.map(agentDevices.getContent());
      return new PageImpl<>(agentDeviceDTOs, pageable, agentDevices.getTotalElements());
    }
    return null;
  }
}
