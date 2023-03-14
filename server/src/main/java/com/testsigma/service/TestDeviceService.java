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
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.TestDeviceCloudXMLDTO;
import com.testsigma.dto.export.TestDeviceXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.ExportTestDeviceMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestDeviceRepository;
import com.testsigma.repository.TestDeviceSuiteRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestDeviceSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TestDeviceService extends XMLExportImportService<TestDevice> {

  private final TestDeviceRepository testDeviceRepository;
  private final TestDeviceSuiteService suiteMappingService;
  private final TestDeviceSuiteRepository testDeviceSuiteRepository;
  private final ExportTestDeviceMapper mapper;
  private final TestPlanService testPlanService;
  private final UploadVersionService uploadVersionService;

  public List<TestDevice> findByWorkspaceIdAndAppUploadId(Long workspaceId, Long appUploadId) {
    return testDeviceRepository.findByWorkspaceVersionWorkspaceIdAndAppUploadId(workspaceId, appUploadId);
  }

  public List<TestDevice> findByTargetMachine(Long agentId) {
    return testDeviceRepository.findTestDeviceByAgentId(agentId);
  }

  public List<TestDevice> findByTestPlanIdAndDisable(Long testPlanId, Boolean disable) {
    return testDeviceRepository.findByTestPlanIdAndDisable(testPlanId, disable);
  }

  public List<TestDevice> findByTestPlanId(Long testPlanId) {
    return testDeviceRepository.findByTestPlanId(testPlanId);
  }

  public TestDevice find(Long id) throws TestsigmaDatabaseException {
    return testDeviceRepository.findById(id).orElseThrow(
      () -> new TestsigmaDatabaseException("Could not find resource with id:" + id));
  }


  public Page<TestDevice> findAll(Specification<TestDevice> spec, Pageable pageable) {
    return this.testDeviceRepository.findAll(spec, pageable);
  }

  public TestDevice create(TestDevice testDevice) {
    Long prerequisiteTestDevicesIdIndex = testDevice.getPrerequisiteTestDevicesIdIndex();
    testDevice.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testDevice = this.testDeviceRepository.save(testDevice);
    testDevice.setPrerequisiteTestDevicesIdIndex(prerequisiteTestDevicesIdIndex);
    this.handleEnvironmentSuiteMappings(testDevice);
    return testDevice;
  }

  public TestDevice update(TestDevice testDevice) {
    testDevice.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    List<Long> suiteIds = testDevice.getSuiteIds();
    testDevice = this.testDeviceRepository.save(testDevice);
    testDevice.setSuiteIds(suiteIds);
    this.handleEnvironmentSuiteMappings(testDevice);
    //this.suiteMappingService.save(executionEnvironment);
    return testDevice;
  }

  public void updateUploadVersion(Upload upload) {
    List<TestDevice> testDevices = findByWorkspaceIdAndAppUploadId(upload.getWorkspace().getId(), upload.getId());
    for(TestDevice testDevice : testDevices) {
      testDevice.setAppUploadVersionId(upload.getLatestVersionId());
      update(testDevice);
    }
  }

  public void delete(Set<Long> executionEnvironmentIds) {
    this.testDeviceRepository.deleteAllByIds(executionEnvironmentIds);
  }

  public void handleEnvironmentSuiteMappings(TestDevice testDevice) {
    int position = 0;
    List<TestDeviceSuite> newMappings = new ArrayList<>();
    List<TestDeviceSuite> updatedMappings = new ArrayList<>();
    this.cleanupOrphanSuiteMappings(testDevice);
    if (testDevice.getSuiteIds().size() > 0) {
      List<TestDeviceSuite> mappings = this.testDeviceSuiteRepository.findByTestDeviceIdAndSuiteIds(testDevice.getId(), testDevice.getSuiteIds());
      for (Long suiteId : testDevice.getSuiteIds()) {
        TestDeviceSuite testDeviceSuite = new TestDeviceSuite();
        Optional<TestDeviceSuite> existing = mappings.stream().filter(mapping -> mapping.getSuiteId().equals(suiteId)).findFirst();
        position++;
        if (existing.isPresent()) {
          testDeviceSuite = existing.get();
          if (!testDeviceSuite.getPosition().equals(position)) {
            testDeviceSuite.setPosition(position);
            testDeviceSuite = this.suiteMappingService.update(testDeviceSuite);
            updatedMappings.add(testDeviceSuite);
          }
        } else {
          testDeviceSuite.setTestDeviceId(testDevice.getId());
          testDeviceSuite.setSuiteId(suiteId);
          testDeviceSuite.setPosition(position);
          testDeviceSuite = this.suiteMappingService.add(testDeviceSuite);
          newMappings.add(testDeviceSuite);
        }
      }
    }
    testDevice.setUpdatedSuiteIds(updatedMappings);
    testDevice.setAddedSuiteIds(newMappings);
  }

  private void cleanupOrphanSuiteMappings(TestDevice testDevice) {
    List<Long> suiteIdsFromUI = testDevice.getSuiteIds();
    List<TestDeviceSuite> existingSuites = suiteMappingService.findAllByTestDeviceId(testDevice.getId());
    List<Long> existingSuiteIds = existingSuites.stream().map(TestDeviceSuite::getSuiteId).collect(Collectors.toList());
    if (suiteIdsFromUI == null) {
      suiteIdsFromUI = existingSuiteIds;
      testDevice.setSuiteIds(existingSuiteIds);
    }
    existingSuiteIds.removeAll(suiteIdsFromUI);
    if (existingSuiteIds.size() > 0)
      this.deleteAllByTestDeviceAndSuiteIds(testDevice, existingSuiteIds);
  }


  private void deleteAllByTestDeviceAndSuiteIds(TestDevice testDevice, List<Long> existingSuiteIds) {
    List<TestDeviceSuite> mappings = this.testDeviceSuiteRepository.findByTestDeviceIdAndSuiteIds(testDevice.getId(), existingSuiteIds);
    testDevice.setRemovedSuiteIds(mappings);
    this.suiteMappingService.deleteAll(mappings);
  }


  public List<TestDevice> findAllByAgentDeviceIds(List<Long> removedAgentDeviceIds) {
    return this.testDeviceRepository.findAllByDeviceIdIn(removedAgentDeviceIds);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestDeviceEnabled()) return;
    log.debug("backup process for execution environment initiated");
    writeXML("test_devices", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for execution environment completed");
  }

  @Override
  protected List<TestDeviceXMLDTO> mapToXMLDTOList(List<TestDevice> list) {
    return mapper.mapEnvironments(list);
  }

  public Specification<TestDevice> getExportXmlSpecification(BackupDTO backupDTO) {
    List<TestPlan> testPlanList = testPlanService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
    List<Long> testPlanIds = testPlanList.stream().map(execution -> execution.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("testPlanId", SearchOperation.IN, testPlanIds);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestDeviceSpecificationsBuilder testDeviceSpecificationsBuilder = new TestDeviceSpecificationsBuilder();
    testDeviceSpecificationsBuilder.params = params;
    return testDeviceSpecificationsBuilder.build();
  }

  public void resentAppUploadIdToNull(Long appUploadId){
    this.testDeviceRepository.resentAppUploadIdToNull(appUploadId);
  }

  public void resetAgentIdToNull(Long agentId) {
    this.testDeviceRepository.resetAgentIdToNull(agentId);
  }

  public List<TestDevice> findAllByTestSuiteId(Long id) {
    return testDeviceRepository.findAllByTestSuiteId(id);
  }

  public void importXML(BackupDTO importDTO) throws IOException,
          ResourceNotFoundException {
    if (!importDTO.getIsTestDeviceEnabled()) return;
    log.debug("import process for test devices initiated");
    if (importDTO.getIsCloudImport())
    importFiles("test_machines", importDTO);
    else
      importFiles("test_devices", importDTO);
    log.debug("import process for test devices completed");
  }

  @Override
  public List<TestDevice> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException, ResourceNotFoundException {

    if (importDTO.getIsSameApplicationType() && importDTO.getIsCloudImport()) {
      return mapper.mapTestDevicesCloudXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<TestDeviceCloudXMLDTO>>() {
      }));
    }
    else if (importDTO.getIsSameApplicationType()  && !importDTO.getIsCloudImport()){
      return mapper.mapTestDevicesXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<TestDeviceXMLDTO>>() {
      }));
    }
    else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<TestDevice> findImportedEntity(TestDevice executionEnvironment, BackupDTO importDTO) {
    Optional<TestPlan> execution = testPlanService.getRecentImportedEntity(importDTO, executionEnvironment.getTestPlanId());
    return testDeviceRepository.findAllByTestPlanIdAndImportedId(execution.get().getId(), executionEnvironment.getTestPlanId());
  }

  @Override
  public TestDevice processBeforeSave(Optional<TestDevice> previous, TestDevice present, TestDevice toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    Optional<TestPlan> execution = testPlanService.getRecentImportedEntity(importDTO, present.getTestPlanId());
    Optional<UploadVersion> uploadVersion = uploadVersionService.getRecentImportedEntity(importDTO, present.getAppUploadVersionId());
    if(uploadVersion.isPresent()){
      present.setAppUploadId(uploadVersion.get().getUploadId());
      present.setAppUploadVersionId(uploadVersion.get().getId());
    }
    present.setTestPlanId(execution.get().getId());
    return present;
  }

  @Override
  public boolean hasToSkip(TestDevice executionEnvironment, BackupDTO importDTO) {
    Optional<TestPlan> execution = testPlanService.getRecentImportedEntity(importDTO, executionEnvironment.getTestPlanId());
    return !importDTO.getIsSameApplicationType() || execution.isEmpty();
  }

  @Override
  void updateImportedId(TestDevice executionEnvironment, TestDevice previous, BackupDTO importDTO) {
    previous.setImportedId(executionEnvironment.getId());
    save(previous);
  }

  @Override
  public TestDevice copyTo(TestDevice executionEnvironment) {
    return mapper.copy(executionEnvironment);
  }

  public TestDevice save(TestDevice executionEnvironment) {
    executionEnvironment = testDeviceRepository.save(executionEnvironment);
    return executionEnvironment;
  }

  @Override
  public Optional<TestDevice> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    List<Long> executionIds = testPlanService.findAllByWorkspaceVersionId(importDTO.getWorkspaceVersionId()).stream().map(execution -> execution.getId()).collect(Collectors.toList());
    return testDeviceRepository.findAllByTestPlanIdInAndImportedId(executionIds, importedId);
  }

  public Optional<TestDevice> findImportedEntityHavingSameName(Optional<TestDevice> previous, TestDevice current, BackupDTO importDTO) {
    Optional<TestPlan> execution = testPlanService.getRecentImportedEntity(importDTO, current.getTestPlanId());
    Optional<TestDevice> oldEntity = testDeviceRepository.findAllByTestPlanIdAndTitle(execution.get().getId(), current.getTitle());
    return oldEntity;
  }

  public boolean hasImportedId(Optional<TestDevice> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestDevice> previous, TestDevice current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }
}
