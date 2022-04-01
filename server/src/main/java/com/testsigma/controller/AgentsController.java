/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.config.ApplicationConfig;
import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.AgentDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AgentMapper;
import com.testsigma.model.Agent;
import com.testsigma.model.TestDevice;
import com.testsigma.service.AgentService;
import com.testsigma.service.JWTTokenService;
import com.testsigma.service.TestDeviceService;
import com.testsigma.specification.AgentSpecificationsBuilder;
import com.testsigma.web.request.AgentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/settings/agents")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AgentsController {

  private final AgentMapper agentMapper;
  private final AgentService agentService;
  private final TestDeviceService testDeviceService;
  private final JWTTokenService jwtTokenService;
  private final ApplicationConfig applicationConfig;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public AgentDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Agent agent = agentService.find(id);
    return agentMapper.map(agent);
  }

  @RequestMapping(path = "/{uuid}/uuid", method = RequestMethod.GET)
  public AgentDTO showByUUID(@PathVariable("uuid") String uuid) throws ResourceNotFoundException {
    Agent agent = agentService.findByUniqueId(uuid);
    return agentMapper.map(agent);
  }

  @RequestMapping(method = RequestMethod.POST)
  public AgentDTO create(@RequestBody @Valid AgentRequest agentRequest) throws TestsigmaException {
    Agent agent = agentService.create(agentRequest);
    AgentDTO agentDTO = agentMapper.map(agent);
    agentDTO.setJwtApiKey(agent.generateJwtApiKey(jwtTokenService.getServerUuid()));
    return agentDTO;
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<AgentDTO> index(AgentSpecificationsBuilder builder, @PageableDefault(value = 100) Pageable pageable) {
    Specification<Agent> specification = builder.build();
    Page<Agent> agents = agentService.findAll(specification, pageable);
    List<AgentDTO> dtos = agentMapper.map(agents.getContent());
    return new PageImpl<>(dtos, pageable, agents.getTotalElements());
  }

  @GetMapping(value = "/all")
  public Page<AgentDTO> findAll(AgentSpecificationsBuilder builder, @PageableDefault(value = 100) Pageable pageable) {
    Specification<Agent> specification = builder.buildAll();
    Page<Agent> agents = agentService.findAll(specification, pageable);
    List<AgentDTO> dtos = agentMapper.map(agents.getContent());
    return new PageImpl<>(dtos, pageable, agents.getTotalElements());
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  public AgentDTO update(@RequestBody AgentRequest agentRequest, @PathVariable("id") Long id)
    throws ResourceNotFoundException {
    Agent agent = agentService.find(id);
    agent = agentService.update(agentRequest, agent.getUniqueId());
    return agentMapper.map(agent);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<String> delete(@PathVariable("id") Long agentId) throws ResourceNotFoundException {
    testDeviceService.resetAgentIdToNull(agentId);
    Agent agent = agentService.find(agentId);
    final List<TestDevice> testDeviceList =
      testDeviceService.findByTargetMachine(agent.getId());
    if (testDeviceList.isEmpty()) {
      agentService.destroy(agent);
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      String message = com.testsigma.constants.MessageConstants.getMessage(
        MessageConstants.AGENT_DELETE_LINKED_ENVIRONMENTS,
        testDeviceList.stream().map(TestDevice::getTitle).collect(Collectors.joining(" , "))
      );
      return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(path = "/download_tag", method = RequestMethod.GET)
  public Map<String, Object> downloadTag() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("tag", applicationConfig.getLocalAgentDownloadTag());
    return jsonObject.toMap();
  }
}
