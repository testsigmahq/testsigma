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
import com.testsigma.dto.export.TestDataCloudXMLDTO;
import com.testsigma.dto.export.TestDataXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestDataEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.model.Element;
import com.testsigma.model.TestData;
import com.testsigma.repository.TestDataProfileRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestDataProfileSpecificationsBuilder;
import com.testsigma.web.request.TestDataSetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class TestDataProfileService extends XMLExportImportService<TestData> {
  private final TestDataProfileRepository testDataProfileRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TestDataProfileMapper mapper;

  public TestData find(Long id) throws ResourceNotFoundException {
    return testDataProfileRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Test Data Not Found with id: " + id));
  }

  public TestData findTestDataByTestCaseId(Long testCaseId) throws ResourceNotFoundException {
    return testDataProfileRepository.findTestDataByTestCaseId(testCaseId).orElseThrow(() -> new ResourceNotFoundException(
      "Test Data Not Found with testCaseId: " + testCaseId));
  }

  public Page<TestData> findAll(Specification<TestData> spec, Pageable pageable) {
    return this.testDataProfileRepository.findAll(spec, pageable);
  }

  public List<TestData> findAllByVersionId(Long workspaceVersionId) {
    return this.testDataProfileRepository.findAllByVersionId(workspaceVersionId);
  }

  public TestData create(TestData testData) {
    testData = this.testDataProfileRepository.save(testData);
    publishEvent(testData, EventType.CREATE);
    return testData;
  }

  public TestData update(TestData testData) {
    Map<String, String> renamedColumns = testData.getRenamedColumns();
    testData = testDataProfileRepository.save(testData);
    testData.setRenamedColumns(renamedColumns);
    publishEvent(testData, EventType.UPDATE);
    return testData;
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestData testData = this.find(id);
    Long count = testDataProfileRepository.countAllTestDataProfilesUsedInForLoopSteps(testData.getVersionId(),id);
    if (count > 0) throw new DataIntegrityViolationException("Cannot delete or update a parent row");
    this.testDataProfileRepository.delete(testData);
    publishEvent(testData, EventType.DELETE);
  }

  public void bulkDestroy(Long[] ids) throws Exception {
    Boolean allIdsDeleted = true;
    Exception throwable = new Exception();
    for (Long id : ids) {
      try {
        destroy(id);
      } catch (Exception ex) {
        allIdsDeleted = false;
        throwable = ex;
      }
    }
    if (!allIdsDeleted) {
      throw throwable;
    }
  }

  public void publishEvent(TestData testData, EventType eventType) {
    TestDataEvent<TestData> event = createEvent(testData, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestDataEvent<TestData> createEvent(TestData testData, EventType eventType) {
    TestDataEvent<TestData> event = new TestDataEvent<>();
    event.setEventData(testData);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestDataEnabled()) return;
    log.debug("backup process for test data initiated");
    writeXML("test_data", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for test data completed");
  }

  @Override
  protected List<TestDataXMLDTO> mapToXMLDTOList(List<TestData> list) {
    return mapper.mapTestData(list);
  }

  public Specification<TestData> getExportXmlSpecification(BackupDTO backupDTO) {
    SearchCriteria criteria = new SearchCriteria("versionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestDataProfileSpecificationsBuilder testDataProfileSpecificationsBuilder = new TestDataProfileSpecificationsBuilder();
    testDataProfileSpecificationsBuilder.params = params;
    return testDataProfileSpecificationsBuilder.build();
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsTestDataEnabled()) return;
    log.debug("import process for Test Data initiated");
    importFiles("test_data", importDTO);
    log.debug("import process for Test Data completed");
  }

  @Override
  public List<TestData> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapTestDataList(xmlMapper.readValue(xmlData, new TypeReference<List<TestDataXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapCloudTestDataList(xmlMapper.readValue(xmlData, new TypeReference<List<TestDataCloudXMLDTO>>() {
      }));
    }
  }

  @Override
  public Optional<TestData> findImportedEntity(TestData testData, BackupDTO importDTO) {
    return testDataProfileRepository.findAllByVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), testData.getId());
  }

  @Override
  public TestData processBeforeSave(Optional<TestData> previous, TestData present, TestData toImport, BackupDTO importDTO) {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    present.setVersionId(importDTO.getWorkspaceVersionId());
    return present;
  }


  @Override
  public TestData copyTo(TestData testData) {
    return mapper.copy(testData);
  }

  @Override
  public TestData save(TestData testData) {
    return testDataProfileRepository.save(testData);
  }

  @Override
  public Optional<TestData> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return testDataProfileRepository.findAllByVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), importedId);
  }

  public Optional<TestData> findImportedEntityHavingSameName(Optional<TestData> previous, TestData current, BackupDTO importDTO) {
    Optional<TestData> oldEntity = testDataProfileRepository.findByTestDataNameAndVersionId(current.getTestDataName(),importDTO.getWorkspaceVersionId());
    return oldEntity;
  }

  public boolean hasImportedId(Optional<TestData> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestData> previous, TestData current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(TestData testData, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(TestData testData, TestData previous, BackupDTO importDTO) {
    previous.setImportedId(testData.getId());
    save(previous);
  }

  public boolean isDataSetNameEmpty(TestDataSetRequest dataSetRequest) {
    return (dataSetRequest.getName() == null || dataSetRequest.getName().isEmpty());
  }

}
