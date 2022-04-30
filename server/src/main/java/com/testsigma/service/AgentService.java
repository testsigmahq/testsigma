/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.AgentCloudXMLDTO;
import com.testsigma.dto.export.AgentXMLDTO;
import com.testsigma.event.AgentEvent;
import com.testsigma.event.EventType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AgentMapper;
import com.testsigma.model.Agent;
import com.testsigma.model.AgentDevice;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestPlan;
import com.testsigma.repository.AgentRepository;
import com.testsigma.specification.AgentSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestDeviceSpecificationsBuilder;
import com.testsigma.util.HttpClient;
import com.testsigma.web.request.AgentRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class AgentService extends XMLExportImportService<Agent> {

  private final AgentRepository agentRepository;
  private final AgentMapper mapper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final AgentDeviceService agentDeviceService;
  private final TestPlanService testPlanService;
  private final TestDeviceService testDeviceService;
  private final AgentMapper exportAgentMapper;
  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;
  private final JWTTokenService jwtTokenService;


  public Agent find(Long id) throws ResourceNotFoundException {
    return agentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Agent is not found with id:" + id));
  }

  public Agent create(@NonNull Agent agent) {
    agent.setUpdatedDate(new Timestamp(new Date().getTime()));
    agent.setCreatedDate(new Timestamp(new Date().getTime()));
    agent.setUniqueId(UUID.randomUUID().toString());
    agent = agentRepository.save(agent);
    return agent;
  }

  public Agent create(@NonNull AgentRequest agentRequest) throws TestsigmaException {
    Agent agent = mapper.map(agentRequest);
    agent = create(agent);
    return agent;
  }

  public void createLocalAgent(Agent agent) throws TestsigmaException {
    agent = create(agent);
    String url = applicationConfig.getLocalAgentUrl() +"/api/v1/" + agent.getUniqueId() + "/register?jwtApiKey="
      + agent.generateJwtApiKey(jwtTokenService.getServerUuid());
    httpClient.put(url, getHeaders(), new JSONObject(), new TypeReference<>() {
    });
  }

  public Page<Agent> findAll(Specification<Agent> specification, Pageable pageable) {
    return agentRepository.findAll(specification, pageable);
  }

  public void destroy(@NonNull Agent agent) {
    List<AgentDevice> agentDevices = agentDeviceService.findAllByAgent(agent.getId());
    agentDevices.forEach(agentDevice -> agentDeviceService.publishEvent(agentDevice, EventType.DELETE));
    agentRepository.delete(agent);
    publishEvent(agent, EventType.DELETE);
  }

  public boolean isAgentActive(Long agentId) throws ResourceNotFoundException {
    Agent agent = find(agentId);
    long lastUpdatedTime = agent.getUpdatedDate().getTime();
    long currentTime = java.lang.System.currentTimeMillis();
    return currentTime - lastUpdatedTime <= 10 * 60 * 1000;
  }

  public Agent findByUniqueId(@NonNull String uniqueId) throws ResourceNotFoundException {
    Agent agent = null;
    try {
      agent = agentRepository.findByUniqueId(uniqueId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    if (agent == null) {
      throw new ResourceNotFoundException("Agent is not found");
    }
    return agent;
  }

  public Agent update(@NonNull AgentRequest agentRequest, String uniqueId) throws ResourceNotFoundException {
    boolean isRegistered = false;
    Agent db = findByUniqueId(uniqueId);
    if (db.getOsType() != null) {
      isRegistered = true;
    }
    mapper.map(agentRequest, db);
    db.setUpdatedDate(new Timestamp(java.lang.System.currentTimeMillis()));
    db = agentRepository.save(db);
    if (!isRegistered && db.getOsType() != null) {
      publishEvent(db, EventType.CREATE);
    }
    return db;
  }

  public void publishEvent(Agent agent, EventType eventType) {
    AgentEvent<Agent> event = createEvent(agent, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public AgentEvent<Agent> createEvent(Agent agent, EventType eventType) {
    AgentEvent<Agent> event = new AgentEvent<>();
    event.setEventData(agent);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsAgentEnabled()) return;
    log.debug("backup process for agent initiated");
    writeXML("agent", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for agent completed");
  }

  @Override
  protected List<AgentXMLDTO> mapToXMLDTOList(List<Agent> list) {
    return exportAgentMapper.mapAgents(list);
  }

  @Override
  public Specification<Agent> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    List<TestPlan> testPlanList = testPlanService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
    List<Long> testPlanIds = testPlanList.stream().map(testPlan -> testPlan.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("testPlanId", SearchOperation.IN, testPlanIds);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestDeviceSpecificationsBuilder testDeviceSpecificationsBuilder = new TestDeviceSpecificationsBuilder();
    testDeviceSpecificationsBuilder.params = params;
    Page<TestDevice> page = testDeviceService.findAll(testDeviceSpecificationsBuilder.build(), PageRequest.of(0, 100));
    List<Long> agentIds = page.getContent().stream().map(testDevice -> {
      if (testDevice.getAgent() != null) {
        return testDevice.getAgent().getId();
      }
      return null;
    }).collect(Collectors.toList());
    AgentSpecificationsBuilder agentSpecificationsBuilder = new AgentSpecificationsBuilder();
    SearchCriteria systems = new SearchCriteria("id", SearchOperation.IN, agentIds);
    params = new ArrayList<>();
    params.add(systems);
    agentSpecificationsBuilder.params = params;
    return agentSpecificationsBuilder.build();
  }

  private ArrayList<Header> getHeaders() {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    return headers;
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsAgentEnabled()) return;
    log.debug("import process for agent initiated");
    importFiles("agent", importDTO);
    log.debug("import process for agent completed");
  }

  @Override
  public List<Agent> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapCloudXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<AgentCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<AgentXMLDTO>>() {
      }));
    }
  }

  @Override
  public Optional<Agent> findImportedEntity(Agent agent, BackupDTO importDTO) {
    Optional<Agent> previous = agentRepository.findAllByImportedId(agent.getId());
    return previous;
  }

  @Override
  public Agent processBeforeSave(Optional<Agent> previous, Agent present, Agent toImport, BackupDTO importDTO) {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    return present;
  }

  @Override
  public Agent copyTo(Agent testCase) {
    return mapper.copy(testCase);
  }


  public Agent save(Agent testCase) {
    testCase = agentRepository.save(testCase);
    return testCase;
  }

  @Override
  public Optional<Agent> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return agentRepository.findAllByImportedId(importedId);
  }

  @Override
  public Optional<Agent> findImportedEntityHavingSameName(Optional<Agent> previous, Agent current, BackupDTO importDTO) {
    return previous;
  }

  @Override
  public boolean hasImportedId(Optional<Agent> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  @Override
  public boolean isEntityAlreadyImported(Optional<Agent> previous, Agent current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(Agent agent, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(Agent agent, Agent previous, BackupDTO importDTO) {
    previous.setImportedId(agent.getId());
    save(previous);
  }
}
