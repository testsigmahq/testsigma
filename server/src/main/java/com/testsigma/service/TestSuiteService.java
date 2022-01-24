/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestSuiteXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestSuiteEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestSuiteMapper;
import com.testsigma.model.*;
import com.testsigma.repository.SuiteTestCaseMappingRepository;
import com.testsigma.repository.TagRepository;
import com.testsigma.repository.TestSuiteRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestPlanSpecificationsBuilder;
import com.testsigma.specification.TestSuiteSpecificationsBuilder;
import lombok.Data;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Data
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestSuiteService extends XMLExportService<TestSuite> {

  private final TestSuiteRepository repository;
  private final TagRepository tagRepository;
  private final TagService tagService;
  private final SuiteTestCaseMappingService suiteTestCaseMappingService;
  private final SuiteTestCaseMappingRepository suiteTestCaseMappingRepository;
  private final TestPlanService testPlanService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TestSuiteMapper mapper;

  public Page<TestSuite> findAll(Specification spec, Pageable pageble) {
    return repository.findAll(spec, pageble);
  }

  public List<AbstractTestSuite> findAllByTestDeviceId(Long environmentId) {
    return this.repository.findAllByTestDeviceId(environmentId);
  }

  public List<TestSuite> findByPrerequisiteId(Long prerequisite) {
    return this.repository.findAllByPreRequisite(prerequisite);
  }

  public TestSuite find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException(
        "TestSuite Resource not found with id:" + id));
  }

  public TestSuite create(TestSuite testSuite) throws TestsigmaException {
    List<String> tagNames = testSuite.getTags();
    List<Long> testCaseIds = testSuite.getTestCaseIds();
    testSuite.setTags(tagNames);
    List<Long> prereq = new ArrayList<>();
    prereq.add(testSuite.getId());
    validatePreRequisiteIsValid(testSuite, prereq);
    testSuite = this.repository.save(testSuite);
    testSuite.setTestCaseIds(testCaseIds);
    tagService.updateTags(testSuite.getTags(), TagType.TEST_SUITE, testSuite.getId());
    this.handleTestCaseMappings(testSuite);
    publishEvent(testSuite, EventType.CREATE);
    return testSuite;
  }

  public TestSuite update(TestSuite testSuite) throws TestsigmaException {
    List<Long> testCaseIds = testSuite.getTestCaseIds();
    List<String> tagNames = testSuite.getTags();
    List<Long> prereq = new ArrayList<>();
    prereq.add(testSuite.getId());
    validatePreRequisiteIsValid(testSuite, prereq);
    testSuite = this.repository.save(testSuite);
    testSuite.setTestCaseIds(testCaseIds);
    testSuite.setTags(tagNames);
    if (testSuite.getTags() != null)
      tagService.updateTags(testSuite.getTags(), TagType.TEST_SUITE, testSuite.getId());
    this.handleTestCaseMappings(testSuite);
    publishEvent(testSuite, EventType.UPDATE);
    return testSuite;
  }

  private void validatePreRequisiteIsValid(TestSuite testSuite, List<Long> preReqList) throws TestsigmaException {
    Long preRequsiteId = testSuite.getPreRequisite();
    if (preRequsiteId != null) {
      if (preReqList.size() > 5) {
        log.debug("Testsuite Prerequisite hierarchy is more than 5,Prerequisite IDs:" + preReqList);
        throw new TestsigmaException("Prerequisite hierarchy crossed the allowed limit of 5");
      } else if (preReqList.contains(testSuite.getPreRequisite())) {
        log.debug("Cyclic dependency for Testsuite prerequisites found for Testsuite:" + testSuite);
        throw new TestsigmaException("Prerequisite to the Testsuite is not valid. This prerequisite causes cyclic dependencies for Testsuites.");
      }
      preReqList.add(preRequsiteId);
      TestSuite preReqTestSuite = find(preRequsiteId);
      if (preReqTestSuite.getPreRequisite() != null) {
        validatePreRequisiteIsValid(preReqTestSuite, preReqList);
      }
    } else {
      return;
    }
  }

  public TestSuite updateSuite(TestSuite testSuite) {
    testSuite = this.repository.save(testSuite);
    publishEvent(testSuite, EventType.UPDATE);
    return testSuite;
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestSuite testSuite = this.find(id);
    publishEvent(testSuite, EventType.DELETE);
    this.repository.deleteById(id);
  }

  private void handleTestCaseMappings(TestSuite testSuite) {
    int position = 0;
    List<SuiteTestCaseMapping> newMappings = new ArrayList<>();
    List<SuiteTestCaseMapping> updatedMappings = new ArrayList<>();
    this.cleanupOrphanCaseMappings(testSuite);
    List<SuiteTestCaseMapping> mappings = this.suiteTestCaseMappingRepository.findBySuiteIdAndTestCaseIds(testSuite.getId(), testSuite.getTestCaseIds());
    for (Long testCaseId : testSuite.getTestCaseIds()) {
      SuiteTestCaseMapping suiteTestCaseMapping = new SuiteTestCaseMapping();
      Optional<SuiteTestCaseMapping> existing = mappings.stream().filter(mapping -> mapping.getTestCaseId().equals(testCaseId)).findFirst();
      position++;
      if (existing.isPresent()) {
        suiteTestCaseMapping = existing.get();
        if (!suiteTestCaseMapping.getPosition().equals(position)) {
          suiteTestCaseMapping.setPosition(position);
          suiteTestCaseMapping = this.suiteTestCaseMappingService.update(suiteTestCaseMapping);
          updatedMappings.add(suiteTestCaseMapping);
        }
      } else {
        suiteTestCaseMapping.setSuiteId(testSuite.getId());
        suiteTestCaseMapping.setTestCaseId(testCaseId);
        suiteTestCaseMapping.setPosition(position);
        suiteTestCaseMapping = this.suiteTestCaseMappingService.add(suiteTestCaseMapping);
        newMappings.add(suiteTestCaseMapping);
      }
    }
    testSuite.setUpdatedTestCases(updatedMappings);
    testSuite.setAddedTestCases(newMappings);
  }

  private void cleanupOrphanCaseMappings(TestSuite testSuite) {
    List<SuiteTestCaseMapping> suiteTestCaseMappings = this.suiteTestCaseMappingService.findAllBySuiteId(testSuite.getId());
    List<Long> existingCaseIds = suiteTestCaseMappings.stream().map(SuiteTestCaseMapping::getTestCaseId).collect(Collectors.toList());
    existingCaseIds.removeAll(testSuite.getTestCaseIds());
    if (existingCaseIds.size() > 0) {
      this.deleteAllBySuiteIdAndCaseIds(testSuite, existingCaseIds);
      List<Long> testCaseIds = testSuite.getTestCaseIds();
      testCaseIds.removeAll(existingCaseIds);
      testSuite.setTestCaseIds(testCaseIds);
    }
  }

  private void deleteAllBySuiteIdAndCaseIds(TestSuite suite, List<Long> existingCaseIds) {
    List<SuiteTestCaseMapping> mappings = this.suiteTestCaseMappingRepository.findBySuiteIdAndTestCaseIds(suite.getId(), existingCaseIds);
    suite.setRemovedTestCases(mappings);
    this.suiteTestCaseMappingService.deleteAll(mappings);
  }

  public void bulkDelete(Long[] ids) throws Exception {
    Boolean allIdsDeleted = true;
    TestPlanSpecificationsBuilder builder = new TestPlanSpecificationsBuilder();
    for (Long id : ids) {
      List<SearchCriteria> params = new ArrayList<>();
      params.add(new SearchCriteria("suiteId", SearchOperation.EQUALITY, id));
      builder.setParams(params);
      Specification<TestPlan> spec = builder.build();
      Page<TestPlan> linkedTestPlans = testPlanService.findAll(spec, PageRequest.of(0, 1));
      if (linkedTestPlans.getTotalElements() == 0) {
        this.destroy(id);
      } else {
        allIdsDeleted = false;
      }
    }
    if (!allIdsDeleted) {
      throw new DataIntegrityViolationException("dataIntegrityViolationException");
    }
  }

  public void publishEvent(TestSuite testSuite, EventType eventType) {
    TestSuiteEvent<TestSuite> event = createEvent(testSuite, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestSuiteEvent<TestSuite> createEvent(TestSuite testSuite, EventType eventType) {
    TestSuiteEvent<TestSuite> event = new TestSuiteEvent<>();
    event.setEventData(testSuite);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsSuitesEnabled()) return;
    writeXML("test_suites", backupDTO, PageRequest.of(0, 25));
  }

  public Specification<TestSuite> getExportXmlSpecification(BackupDTO backupDTO) {

    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestSuiteSpecificationsBuilder testStepSpecificationsBuilder = new TestSuiteSpecificationsBuilder();
    testStepSpecificationsBuilder.params = params;
    return testStepSpecificationsBuilder.build();
  }

  @Override
  protected List<TestSuiteXMLDTO> mapToXMLDTOList(List<TestSuite> list) {
    return mapper.mapTestSuites(list);
  }
}
