/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestPlanCloudXMLDTO;
import com.testsigma.dto.export.TestPlanXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestPlanEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.TestPlanMapper;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestPlan;
import com.testsigma.repository.TestPlanRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestPlanSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestPlanService extends XMLExportImportService<TestPlan> {

  private final TestPlanRepository testPlanRepository;
  private final TestDeviceService testDeviceService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TestPlanMapper mapper;

  public Optional<TestPlan> findOptional(Long id) {
    return testPlanRepository.findById(id);
  }

  public TestPlan find(Long id) throws TestsigmaDatabaseException {
    return testPlanRepository.findById(id).orElseThrow(() -> new TestsigmaDatabaseException(
      "Could not find resource with id:" + id));
  }

  public TestPlan findById(Long id) throws TestsigmaDatabaseException {
    return testPlanRepository.findById(id).orElse(null);
  }

  public Page<TestPlan> findAll(Specification<TestPlan> spec, Pageable pageable) {
    return this.testPlanRepository.findAll(spec, pageable);
  }

  public List<TestPlan> findAllByWorkspaceVersionId(Long versionId) {
    return this.testPlanRepository.findAllByWorkspaceVersionId(versionId);
  }

  public TestPlan create(TestPlan testPlan) {
    List<TestDevice> environments = testPlan.getTestDevices();
    testPlan = this.testPlanRepository.save(testPlan);
    testPlan.setTestDevices(environments);
    saveExecutionEnvironments(testPlan, false);
    publishEvent(testPlan, EventType.CREATE);
    return testPlan;
  }

  public TestPlan update(TestPlan testPlan) {
    testPlan = this.testPlanRepository.save(testPlan);
    publishEvent(testPlan, EventType.UPDATE);
    return testPlan;
  }

  public TestPlan updateTestPlanAndEnvironments(TestPlan testPlan) {
    saveExecutionEnvironments(testPlan, true);
    return update(testPlan);
  }

  public void destroy(Long id) throws TestsigmaDatabaseException {
    TestPlan testPlan = find(id);
    this.testPlanRepository.delete(testPlan);
    publishEvent(testPlan, EventType.DELETE);
  }

  private void saveExecutionEnvironments(TestPlan testPlan, boolean checkOrphanExecutionEnvironments) {
    if (checkOrphanExecutionEnvironments) {
      Set<Long> orphanTestDeviceIds = testPlan.getOrphanTestDeviceIds();
      if (orphanTestDeviceIds.size() > 0)
        testDeviceService.delete(orphanTestDeviceIds);
    }

    for (TestDevice testDevice : testPlan.getTestDevices()) {
      if (testDevice.getTestPlanId() == null)
        testDevice.setTestPlanId(testPlan.getId());
      if (testDevice.getId() == null) {
        testDeviceService.create(testDevice);
      } else {
        testDeviceService.update(testDevice);
      }
    }
  }

  public void publishEvent(TestPlan testPlan, EventType eventType) {
    TestPlanEvent<TestPlan> event = createEvent(testPlan, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestPlanEvent<TestPlan> createEvent(TestPlan testPlan, EventType eventType) {
    TestPlanEvent<TestPlan> event = new TestPlanEvent<>();
    event.setEventData(testPlan);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestPlanEnabled()) return;
    log.debug("backup process for execution initiated");
    writeXML("test_plans", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for execution completed");
  }

  @Override
  protected List<TestPlanXMLDTO> mapToXMLDTOList(List<TestPlan> list) {
    return mapper.mapToXMLDTOList(list);
  }

  public Specification<TestPlan> getExportXmlSpecification(BackupDTO backupDTO) {
    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestPlanSpecificationsBuilder testPlanSpecificationsBuilder = new TestPlanSpecificationsBuilder();
    testPlanSpecificationsBuilder.params = params;
    return testPlanSpecificationsBuilder.build();
  }

  public void importXML(BackupDTO importDTO) throws
          IOException, ResourceNotFoundException, ResourceNotFoundException {
    if (!importDTO.getIsTestPlanEnabled()) return;
    log.debug("import process for execution  initiated");
    importFiles("test_plans", importDTO);
    log.debug("import process for execution  completed");
  }

  @Override
  public List<TestPlan> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws
          JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapTestPlanCloudList(xmlMapper.readValue(xmlData, new TypeReference<List<TestPlanCloudXMLDTO>>() {
      }));
    }
    else {
      return mapper.mapTestPlanList(xmlMapper.readValue(xmlData, new TypeReference<List<TestPlanXMLDTO>>() {
      }));
    }
  }

  @Override
  public Optional<TestPlan> findImportedEntity(TestPlan execution, BackupDTO importDTO) {
    return testPlanRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), execution.getId());
  }

  @Override
  public TestPlan processBeforeSave(Optional<TestPlan> previous, TestPlan present, TestPlan
          toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    present.setLastRunId(null);
    present.setWorkspaceVersionId(importDTO.getWorkspaceVersionId());
    return present;
  }

  @Override
  public Optional<TestPlan> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return testPlanRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), importedId);
  }

  @Override
  public boolean hasToSkip(TestPlan execution, BackupDTO importDTO) {
    return !importDTO.getIsSameApplicationType();
  }

  @Override
  void updateImportedId(TestPlan execution, TestPlan previous, BackupDTO importDTO) {
    previous.setImportedId(execution.getId());
    save(previous);
  }

  @Override
  public TestPlan copyTo(TestPlan execution) {
    return mapper.copy(execution);
  }

  @Override
  TestPlan save(TestPlan testPlan) {
    return this.testPlanRepository.save(testPlan);
  }

  public Optional<TestPlan> findImportedEntityHavingSameName(Optional<TestPlan> previous, TestPlan
          current, BackupDTO importDTO) {
    Optional<TestPlan> oldEntity = testPlanRepository.findAllByWorkspaceVersionIdAndName(importDTO.getWorkspaceVersionId(), current.getName());
    return oldEntity;
  }

  public boolean hasImportedId(Optional<TestPlan> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestPlan> previous, TestPlan current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

}
