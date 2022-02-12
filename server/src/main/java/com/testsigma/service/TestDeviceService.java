/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestDeviceXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.ExportTestDeviceMapper;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestDeviceSuite;
import com.testsigma.model.TestPlan;
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
public class TestDeviceService extends XMLExportService<TestDevice> {

  private final TestDeviceRepository testDeviceRepository;
  private final TestDeviceSuiteService suiteMappingService;
  private final TestDeviceSuiteRepository testDeviceSuiteRepository;
  private final ExportTestDeviceMapper mapper;
  private final TestPlanService testPlanService;

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
    testDevice.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testDevice = this.testDeviceRepository.save(testDevice);
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

  public void delete(Set<Long> executionEnvironmentIds) {
    this.testDeviceRepository.deleteAllByIds(executionEnvironmentIds);
  }

  private void handleEnvironmentSuiteMappings(TestDevice testDevice) {
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
}
