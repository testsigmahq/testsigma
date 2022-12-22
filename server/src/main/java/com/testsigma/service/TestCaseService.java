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
import com.testsigma.dto.*;
import com.testsigma.dto.export.TestCaseCloudXMLDTO;
import com.testsigma.dto.export.TestCaseXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestCaseEvent;
import com.testsigma.exception.*;
import com.testsigma.mapper.TestCaseMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestCaseRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestCaseSpecificationsBuilder;
import com.testsigma.web.request.TestCaseCopyRequest;
import com.testsigma.web.request.TestCaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
@Log4j2
public class TestCaseService extends XMLExportImportService<TestCase> {
  private final TestCaseMapper testCaseMapper;
  private final com.testsigma.service.TestPlanService testPlanService;
  private final TestDeviceService testDeviceService;
  private final TestDeviceResultService testDeviceResultService;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final TestCaseRepository testCaseRepository;
  private final TagService tagService;
  private final TestStepService testStepService;
  private final TestStepMapper testStepMapper;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final com.testsigma.service.DryTestPlanService dryTestPlanService;
  private final TestCaseMapper mapper;
  private final TestCaseFilterService testCaseFilterService;
  private final StepGroupFilterService stepGroupFilterService;
  private final WorkspaceVersionService workspaceVersionService;
  private final TestSuiteService testSuiteService;
  private final TestCasePriorityService testCasePriorityService;
  private final TestCaseTypeService testCaseTypeService;
  private final TestDataProfileService testDataService;
  private final EntityExternalMappingService entityExternalMappingService;
  private final IntegrationsService integrationsService;

  public Page<TestCase> findAll(Specification<TestCase> specification, Pageable pageable) {
    return testCaseRepository.findAll(specification, pageable);
  }

  public Optional<TestCase> findByNameAndWorkspaceVersionId(String name, Long workspaceVersionId) {
    return testCaseRepository.findByNameAndWorkspaceVersionId(name, workspaceVersionId);
  }

  public List<TestCase> findAllByWorkspaceVersionId(Long workspaceVersionId) {
    return testCaseRepository.findAllByWorkspaceVersionId(workspaceVersionId);
  }

  public Page<TestCase> findAllByWorkspaceVersionIdAndIsStepGroupAndStatus(Long workspaceVersionId, Boolean isStepGroup, TestCaseStatus status, Pageable pageable) {
    return testCaseRepository.findAllByWorkspaceVersionIdAndIsStepGroupAndStatus(workspaceVersionId, isStepGroup, status, pageable);
  }

  public List<TestCase> findAllBySuiteId(Long suiteId) {
    return this.testCaseRepository.findAllBySuiteId(suiteId);
  }

  public Page<TestCase> findAllByTestDataId(Long testDataId, Pageable pageable) {
    return this.testCaseRepository.findAllByTestDataId(testDataId, pageable);
  }

  public Page<TestCase> findAllByPreRequisite(Long preRequisite, Pageable pageable) {
    return this.testCaseRepository.findAllByPreRequisite(preRequisite, pageable);
  }

  public TestCase find(Long id) throws ResourceNotFoundException {
    return testCaseRepository.findById(id).orElseThrow(
      () -> new ResourceNotFoundException("Couldn't find TestCase resource with id:" + id));
  }

  public TestCaseEntityDTO find(Long id, Long environmentResultId, String testDataSetName, Long testCaseResultId) {
    TestCaseEntityDTO testCaseEntityDTO = new TestCaseEntityDTO();
    try {
      TestCase testCase = this.find(id);
      testCaseEntityDTO = testCaseMapper.map(testCase);
      TestDeviceResult testDeviceResult = testDeviceResultService.find(environmentResultId);
      TestDevice testDevice = testDeviceService.find(testDeviceResult.getTestDeviceId());
      Optional<TestPlan> optionalTestPlan = testPlanService.findOptional(testDevice.getTestPlanId());
      AbstractTestPlan testPlan;
      if (optionalTestPlan.isPresent())
        testPlan = optionalTestPlan.get();
      else
        testPlan = dryTestPlanService.find(testDevice.getTestPlanId());
      WorkspaceVersion applicationVersion = testPlan.getWorkspaceVersion();
      Workspace workspace = applicationVersion.getWorkspace();

      testCaseEntityDTO.setTestCaseResultId(testCaseResultId);
      testCaseEntityDTO.setStatus(testDeviceResult.getStatus());
      testCaseEntityDTO.setResult(testDeviceResult.getResult());
      AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
      agentExecutionService.setTestPlan(testPlan);
      agentExecutionService.checkTestCaseIsInReadyState(testCase);
      agentExecutionService
        .loadTestCase(testDataSetName, testCaseEntityDTO, testPlan, testDevice, workspace);
    } catch (TestsigmaNoMinsAvailableException e) {
      log.debug("======= Testcase Error=========");
      log.error(e.getMessage(), e);
      testCaseEntityDTO.setMessage(e.getMessage());
      testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_MINS_VALIDATION_FAILURE);
      return testCaseEntityDTO;
    } catch (TestsigmaException e) {
      log.debug("======= Testcase Error=========");
      log.error(e.getMessage(), e);
      if (e.getErrorCode() != null) {
        if (e.getErrorCode().equals(ExceptionErrorCodes.ENVIRONMENT_PARAMETERS_NOT_CONFIGURED)) {
          testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_ENVIRONMENT_PARAM_FAILURE);
        } else if (e.getErrorCode().equals(ExceptionErrorCodes.ENVIRONMENT_PARAMETER_NOT_FOUND)) {
          testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_ENVIRONMENT_PARAM_FAILURE);
        } else if (e.getErrorCode().equals(ExceptionErrorCodes.TEST_DATA_SET_NOT_FOUND)) {
          testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_TEST_DATA_SET_FAILURE);
        } else if (e.getErrorCode().equals(ExceptionErrorCodes.TEST_DATA_NOT_FOUND)) {
          testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_TEST_DATA_FAILURE);
        } else if (e.getErrorCode().equals(ExceptionErrorCodes.ELEMENT_NOT_FOUND)) {
          testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.ERROR_ELEMENT_FAILURE);
        }
      }
      testCaseEntityDTO.setErrorCode(testCaseEntityDTO.getErrorCode() == null ? ExceptionErrorCodes.UNKNOWN_ERROR : testCaseEntityDTO.getErrorCode());

      testCaseEntityDTO.setMessage(e.getMessage());
      return testCaseEntityDTO;
    } catch (Exception e) {
      log.debug("======= Testcase Error=========");
      log.error(e.getMessage(), e);
      testCaseEntityDTO.setErrorCode(ExceptionErrorCodes.UNKNOWN_ERROR);
      testCaseEntityDTO.setMessage(e.getMessage());
      return testCaseEntityDTO;
    }
    return testCaseEntityDTO;
  }

  public TestCase create(TestCaseRequest testCaseRequest) throws TestsigmaException, SQLException {
    TestCase testCase = testCaseMapper.map(testCaseRequest);
    testCase.setIsActive(true);
    testCase.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setStatusTimeAndBy(testCaseRequest, testCase);
    List<Long> preReqList = new ArrayList<>();
    preReqList.add(testCase.getId());
    validatePreRequisiteIsValid(testCase, preReqList);
    testCase = create(testCase);
    tagService.updateTags(testCaseRequest.getTags(), TagType.TEST_CASE, testCase.getId());
    return testCase;
  }

  public TestCase create(TestCase testCaseRequest) {
    TestCase testCase = testCaseRepository.save(testCaseRequest);
    publishEvent(testCase, EventType.CREATE);
    return testCase;
  }

  public TestCase update(TestCase testCase) {
    testCase = this.testCaseRepository.save(testCase);
    publishEvent(testCase, EventType.UPDATE);
    return testCase;
  }

  public TestCase update(TestCaseRequest testCaseRequest, Long id) throws TestsigmaException, SQLException {
    TestCase testCase = testCaseRepository.findById(id).get();
    Long oldPreRequisite = testCase.getPreRequisite();
    testCase.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setStatusTimeAndBy(testCaseRequest, testCase);
    testCaseMapper.map(testCaseRequest, testCase);
    List<Long> preReqList = new ArrayList<>();
    preReqList.add(testCase.getId());
    validatePreRequisiteIsValid(testCase, preReqList);
    testCase = update(testCase);
    if (testCaseRequest.getTags() != null) {
      tagService.updateTags(testCaseRequest.getTags(), TagType.TEST_CASE, testCase.getId());
    }
    if (testCase.getPreRequisite() != null && !testCase.getPreRequisite().equals(oldPreRequisite)){
        testSuiteService.handlePreRequisiteChange(testCase);
    }
    return testCase;
  }

  private void validatePreRequisiteIsValid(TestCase testCase, List<Long> preReqList) throws TestsigmaException {
    Long preRequisiteId = testCase.getPreRequisite();
    if (preRequisiteId != null) {
      if (preReqList.size() > 5) {
        log.debug("Testcase Prerequisite hierarchy is more than 5,Prerequisite IDs:" + preReqList);
        throw new TestsigmaException("Prerequisite hierarchy crossed the allowed limit of 5");
      } else if (preReqList.contains(testCase.getPreRequisite())) {
        log.debug("Cyclic dependency for Testsuite prerequisites found for Testsuite:" + testCase);
        throw new TestsigmaException("Prerequisite to the TestCase is not valid. This prerequisite causes cyclic dependencies for TestCase.");
      }
      preReqList.add(preRequisiteId);
      TestCase preRequisiteTestCase = find(preRequisiteId);
      if (preRequisiteTestCase.getPreRequisite() != null) {
        validatePreRequisiteIsValid(preRequisiteTestCase, preReqList);
      }
    } else {
      return;
    }

  }


  //TODO:need to revisit this code[chandra]
  private void setStatusTimeAndBy(TestCaseRequest testCaseRequest, TestCase testcase) throws ResourceNotFoundException, TestsigmaDatabaseException, SQLException {
    TestCaseStatus status = testCaseRequest.getStatus();
    Timestamp at = new Timestamp(System.currentTimeMillis());
    if (status.equals(TestCaseStatus.DRAFT)) {
      testCaseRequest.setDraftAt(at);
    } else if (status.equals(TestCaseStatus.IN_REVIEW)) {
      if (!testcase.getStatus().equals(TestCaseStatus.IN_REVIEW)) {
        testCaseRequest.setReviewSubmittedAt(at);
      }
    } else if (status.equals(TestCaseStatus.READY)) {
      if (testcase.getStatus().equals(TestCaseStatus.IN_REVIEW)) {
        testCaseRequest.setReviewedAt(at);
      }
    } else if (status.equals(TestCaseStatus.OBSOLETE)) {
      testCaseRequest.setObsoleteAt(at);
    }
  }

  public Integer markAsDelete(List<Long> ids) {
    return testCaseRepository.markAsDelete(ids);
  }

  public void restore(Long id) {
    testCaseRepository.markAsRestored(id);
  }

  public void destroy(Long id) throws TestsigmaException, IOException {
    TestCase testcase = this.find(id);
    testCaseRepository.delete(testcase);
    Optional<Integrations> testProjectIntegration = integrationsService.findOptionalByApplication(Integration.TestProjectImport);
    if(testProjectIntegration.isPresent()) {
      Optional<EntityExternalMapping> entityExternalMapping =
              entityExternalMappingService.findByEntityIdAndEntityTypeAndApplicationId(testcase.getId(), EntityType.TEST_CASE, testProjectIntegration.get().getId());
      if (entityExternalMapping.isPresent()) {
        entityExternalMappingService.destroy(entityExternalMapping.get());
      }
    }
    publishEvent(testcase, EventType.DELETE);
  }

  public Long automatedCountByVersion(Long versionId) {
    return this.testCaseRepository.countByVersion(versionId);
  }

  public Long testCaseCountByPreRequisite(Long testCaseId){
    return this.testCaseRepository.countByPreRequisite(testCaseId);
  }

  public List<Long> getTestCaseIdsByPreRequisite(Long testCaseId){
    return this.testCaseRepository.getTestCaseIdsByPreRequisite(testCaseId);
  }

  public List<TestCaseStatusBreakUpDTO> breakUpByStatus(Long versionId) {
    return this.testCaseRepository.breakUpByStatus(versionId);
  }

  public List<TestCaseTypeBreakUpDTO> breakUpByType(Long versionId) {
    return this.testCaseRepository.breakUpByType(versionId);
  }

  public TestCase copy(TestCaseCopyRequest testCaseRequest) throws TestsigmaException, SQLException {
    TestCase parentCase = this.find(testCaseRequest.getTestCaseId());
    TestCase testCase = this.testCaseMapper.copy(parentCase);
    testCase.setStatus(parentCase.getStatus());
    testCase.setName(testCaseRequest.getName());
    testCase.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testCase.setLastRunId(null);
    if (testCaseRequest.getIsStepGroup()) {
      testCase.setTestDataStartIndex(null);
      testCase.setTestDataId(null);
      testCase.setIsDataDriven(false);
    }
    testCase.setIsStepGroup(testCaseRequest.getIsStepGroup());
    testCase.setCopiedFrom(parentCase.getId());
    testCase.setPreRequisiteCase(null);
    testCase = create(testCase);
    List<String> tags = tagService.list(TagType.TEST_CASE, parentCase.getId());
    tagService.updateTags(tags, TagType.TEST_CASE, testCase.getId());
    List<TestStep> steps = this.fetchTestSteps(parentCase, testCaseRequest.getStepIds());
    if (steps.size()>0) {
      List<TestStep> newSteps = new ArrayList<>();
      Map<Long, TestStep> parentStepIds = new HashMap<Long, TestStep>();
      Integer position = 0;
      TestStep firstStep = steps.get(0);
      if (firstStep.getConditionType() == TestStepConditionType.LOOP_WHILE) {
        TestStep whileStep = this.testStepService.find(firstStep.getParentId());
        steps.add(0, whileStep);
      }
      for (TestStep parent : steps) {
        if (testCase.getIsStepGroup() && parent.getStepGroupId() != null)
          continue;
        TestStep step = this.testStepMapper.copy(parent);
        step.setPosition(position);
        step.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        step.setTestCaseId(testCase.getId());
        TestStep parentStep = parentStepIds.get(parent.getParentId());
        step.setParentId(parentStep != null ? parentStep.getId() : null);
        TestStep prerequiste = parentStepIds.get(parentStep != null ? parent.getPreRequisiteStepId() : null);
        step.setPreRequisiteStepId(prerequiste != null ? prerequiste.getId() : null);
        step.setId(null);
        TestStep testDataProfileStep = parentStepIds.get(parentStep != null ? step.getTestDataProfileStepId() : null);
        if(testDataProfileStep != null)
          step.setTestDataProfileStepId(testDataProfileStep.getId());
        step.setParentStep(parentStep);
        step = this.testStepService.create(step);
        parentStepIds.put(parent.getId(), step);
        newSteps.add(step);
        position++;
      }
      if(testCaseRequest.getIsReplace() != null && testCaseRequest.getIsReplace().booleanValue()) {
        createAndReplace(steps, testCase, parentCase);
      }
    }
    return testCase;
  }

  private void createAndReplace(List<TestStep> steps, TestCase testCase, TestCase currentTestCase) throws TestsigmaException, SQLException {
    TestStep step = new TestStep();
    step.setPosition(steps.get(0).getPosition());
    step.setTestCaseId(currentTestCase.getId());
    step.setDisabled(false);
    step.setPriority(TestStepPriority.MAJOR);
    step.setParentId(steps.get(0).getParentId() != null ? steps.get(0).getParentId() : null);
    step.setType(TestStepType.STEP_GROUP);
    step.setStepGroupId(testCase.getId());
    step.setId(null);
    if (step.getConditionType() != null && TestStepConditionType.CONDITION_ELSE_IF.equals(step.getConditionType())
            && step.getParentId() == null) {
      step.setConditionType(TestStepConditionType.CONDITION_IF);
    }
    this.testStepService.create(step);
    for (TestStep parent : steps) {
      TestStep destroyStep = this.testStepService.find(parent.getId());
      this.testStepService.destroy(destroyStep);
    }
  }

  private List<TestStep> fetchTestSteps(TestCase testCase, List<Long> stepIds) {
    if (stepIds != null)
      return this.testStepService.findAllByTestCaseIdAndIdIn(testCase.getId(), stepIds);
    else
      return this.testStepService.findAllByTestCaseId(testCase.getId());
  }

  public void publishEvent(TestCase testCase, EventType eventType) {
    TestCaseEvent<TestCase> event = createEvent(testCase, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestCaseEvent<TestCase> createEvent(TestCase testCase, EventType eventType) {
    TestCaseEvent<TestCase> event = new TestCaseEvent<>();
    event.setEventData(testCase);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestCaseEnabled()) return;
    log.debug("backup process for testcase initiated");
    writeXML("testcases", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for testcase completed");
  }

  public Specification<TestCase> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    boolean hasFilter = backupDTO.getFilterId() != null && backupDTO.getFilterId() > 0;
    if (hasFilter) return specificationBuilder(backupDTO);

    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestCaseSpecificationsBuilder testCaseSpecificationsBuilder = new TestCaseSpecificationsBuilder();
    testCaseSpecificationsBuilder.params = params;
    return testCaseSpecificationsBuilder.build();
  }

  private Specification<TestCase> specificationBuilder(BackupDTO backupDTO) throws ResourceNotFoundException {
    ListFilter filter;
    try {
      filter = testCaseFilterService.find(backupDTO.getFilterId());
    } catch (ResourceNotFoundException e) {
      filter = stepGroupFilterService.find(backupDTO.getFilterId());
    }
    WorkspaceVersion version = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    TestCaseSpecificationsBuilder builder = new TestCaseSpecificationsBuilder();
    return builder.build(filter, version);
  }

  @Override
  protected List<TestCaseXMLDTO> mapToXMLDTOList(List<TestCase> list) {
    return mapper.mapTestcases(list);
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsTestCaseEnabled()) return;
    log.debug("import process for testcase initiated");
    importFiles("testcases", importDTO);
    log.debug("import process for testcase completed");
  }

  @Override
  public List<TestCase> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapTestCasesCloudXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<TestCaseCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapTestCasesXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<TestCaseXMLDTO>>() {
      }));
    }
  }
  @Override
  public Optional<TestCase> findImportedEntity(TestCase testCase, BackupDTO importDTO) {
   return testCaseRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), testCase.getId());}

  @Override
  public TestCase processBeforeSave(Optional<TestCase> previous, TestCase present, TestCase toImport, BackupDTO importDTO) {
    present.setImportedId(present.getId());

    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    if (present.getPreRequisite() != null) {
      Optional<TestCase> recentPrerequisite = getRecentImportedEntity(importDTO, present.getPreRequisite());
      if (recentPrerequisite.isPresent())
        present.setPreRequisite(recentPrerequisite.get().getId());
    }
    present.setWorkspaceVersionId(importDTO.getWorkspaceVersionId());

    if (present.getPriority() != null) {
      Optional<TestCasePriority> priority = testCasePriorityService.getRecentImportedEntity(importDTO, present.getPriority());
      if (priority.isPresent())
        present.setPriority(priority.get().getId());
    }

    if (present.getType() != null) {
      Optional<TestCaseType> testCaseType = testCaseTypeService.getRecentImportedEntity(importDTO, present.getType());
      if (testCaseType.isPresent())
        present.setType(testCaseType.get().getId());
    }

    if (present.getTestDataId() != null) {
      Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, present.getTestDataId());
      if (testData.isPresent())
        present.setTestDataId(testData.get().getId());
    }

    present.setLastRunId(null);
    return present;
  }


  @Override
  public TestCase copyTo(TestCase testCase) {
    Long id = testCase.getId();
    testCase = mapper.copy(testCase);
    testCase.setId(id);
    return testCase;
  }


  public TestCase save(TestCase testCase) {
    List<String> tagNames = testCase.getTagNames();
    testCase = testCaseRepository.save(testCase);
    tagService.updateTags(tagNames, TagType.TEST_CASE, testCase.getId());
    return testCase;
  }

  @Override
  public Optional<TestCase> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    Optional<TestCase> previous = testCaseRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), importedId);
    return previous;

  }


  public Optional<TestCase> findImportedEntityHavingSameName(Optional<TestCase> previous, TestCase current, BackupDTO importDTO) {
    Optional<TestCase> oldEntity = testCaseRepository.findTestCaseByWorkspaceVersionIdAndName(importDTO.getWorkspaceVersionId(), current.getName());
    if (oldEntity.isPresent()) {
      return oldEntity;
    } else {
      return Optional.empty();
    }
  }

  public boolean hasImportedId(Optional<TestCase> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestCase> previous, TestCase current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(TestCase testCase, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(TestCase testCase, TestCase previous, BackupDTO importDTO) {
    previous.setImportedId(testCase.getId());
    save(previous);
  }

  public void handlePreRequisiteChange(TestCase testCase) {
    this.testSuiteService.handlePreRequisiteChange(testCase);
  }
}
