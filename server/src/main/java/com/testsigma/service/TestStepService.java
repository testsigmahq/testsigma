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
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestCaseTypeCloudXMLDTO;
import com.testsigma.dto.export.TestCaseTypeXMLDTO;
import com.testsigma.dto.export.TestStepCloudXMLDTO;
import com.testsigma.dto.export.TestStepXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestStepEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RestStepMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestStepRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestStepSpecificationsBuilder;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestStepService extends XMLExportImportService<TestStep> {
  private final TestStepRepository repository;
  private final RestStepService restStepService;
  private final RestStepMapper mapper;
  private final ProxyAddonService addonService;
  private final AddonNaturalTextActionService addonNaturalTextActionService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TestCaseService testCaseService;
  private final TestStepMapper exportTestStepMapper;
  private final TestDataProfileService testDataService;
  private final NaturalTextActionsService naturalTextActionsService;


  public List<TestStep> findAllByTestCaseId(Long testCaseId) {
    return this.repository.findAllByTestCaseIdOrderByPositionAsc(testCaseId);
  }

  public List<TestStep> findAllByTestCaseIdAndEnabled(Long testCaseId) {
    List<TestStep> testSteps = repository.findAllByTestCaseIdAndDisabledIsNotOrderByPositionAsc(testCaseId, true);
    List<TestStep> stepGroups = repository.findAllByTestCaseIdAndDisabledIsNotAndStepGroupIdIsNotNullOrderByPositionAsc(testCaseId, true);
    for (TestStep teststep : stepGroups) {
      if (teststep.getStepGroup() != null) {
        List<TestStep> groupsSteps = repository.findAllByTestCaseIdAndDisabledIsNotOrderByPositionAsc(teststep.getStepGroupId(), true);
        teststep.getStepGroup().setTestSteps(new HashSet<>(groupsSteps));
      }
    }
    return testSteps;
  }

  public List<String> findElementNamesByTestCaseIds(List<Long> testCaseIds) {
    List<String> ElementNames = repository.findTestStepsByTestCaseIdIn(testCaseIds);
    return ElementNames.stream().map(x -> StringUtils.strip(x, "\"")).collect(Collectors.toList());
  }

  public List<String> findAddonActionElementsByTestCaseIds(List<Long> testCaseIds) {
    List<String> elementsNames = new ArrayList<>();
    List<TestStep> testSteps = repository.findAllByTestCaseIdInAndAddonActionIdIsNotNull(testCaseIds);
    for (TestStep step : testSteps) {
      Map<String, AddonElementData> addonElementData = step.getAddonElements();

      for (AddonElementData elementData : addonElementData.values()) {
        elementsNames.add(elementData.getName());
      }
    }
    return elementsNames;
  }

  public Page<TestStep> findAll(Specification<TestStep> spec, Pageable pageable) {
    return this.repository.findAll(spec, pageable);
  }

  public void destroy(TestStep testStep) throws ResourceNotFoundException {
    repository.decrementPosition(testStep.getPosition(), testStep.getTestCaseId());
    repository.delete(testStep);
    if (testStep.getAddonActionId() != null) {
      AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(testStep.getAddonActionId());
      addonService.notifyActionNotUsing(addonNaturalTextAction);
    }
    publishEvent(testStep, EventType.DELETE);
  }

  public TestStep find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestStep missing with id:" + id));
  }
  private TestStep updateDetails(TestStep testStep){
    RestStep restStep = testStep.getRestStep();
    testStep.setRestStep(null);
    testStep = this.repository.save(testStep);
    if (restStep != null) {
      restStep.setStepId(testStep.getId());
      restStep = this.restStepService.update(restStep);
      testStep.setRestStep(restStep);
    }
    return testStep;
  }

  public TestStep update(TestStep testStep) {
    testStep = updateDetails(testStep);
    this.updateDisablePropertyForChildSteps(testStep);
    publishEvent(testStep, EventType.UPDATE);
    return testStep;
  }

  private void updateDisablePropertyForChildSteps(TestStep testStep) {
    List<TestStep> childSteps = this.repository.findAllByParentIdOrderByPositionAsc(testStep.getId());
    if (childSteps.size() > 0) {
      for (TestStep childStep : childSteps) {
        childStep.setDisabled(testStep.getDisabled());
        this.update(childStep);
      }
    }
  }


  public TestStep create(TestStep testStep) throws ResourceNotFoundException {
    this.repository.incrementPosition(testStep.getPosition(), testStep.getTestCaseId());
    RestStep restStep = testStep.getRestStep();
    testStep.setRestStep(null);
    testStep = this.repository.save(testStep);
    if (restStep != null) {
      RestStep newRestStep = mapper.mapStep(restStep);
      newRestStep.setStepId(testStep.getId());
      newRestStep = this.restStepService.create(newRestStep);
      testStep.setRestStep(newRestStep);
    }
    if (testStep.getAddonActionId() != null) {
      AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(testStep.getAddonActionId());
      addonService.notifyActionUsing(addonNaturalTextAction);
    }
    publishEvent(testStep, EventType.CREATE);
    return testStep;
  }

  public void bulkUpdateProperties(Long[] ids, TestStepPriority testStepPriority, Integer waitTime, Boolean disabled, Boolean ignoreStepResult) {
    this.repository.bulkUpdateProperties(ids, testStepPriority != null ? testStepPriority.toString() : null, waitTime);
    if (disabled != null || ignoreStepResult != null)
      this.bulkUpdateDisableAndIgnoreResultProperties(ids, disabled, ignoreStepResult);
  }

  private void bulkUpdateDisableAndIgnoreResultProperties(Long[] ids, Boolean disabled, Boolean ignoreStepResult) {
    List<TestStep> testSteps = this.repository.findAllByIdInOrderByPositionAsc(ids);
    for (TestStep testStep : testSteps) {
      if (disabled != null) {
        if(!disabled && testStep.getParentStep() == null){
          testStep.setDisabled(false);
        } else if(!disabled && testStep.getParentStep() != null){
          testStep.setDisabled(testStep.getParentStep().getDisabled());
        } else {
          testStep.setDisabled(disabled);
        }
      }
      if (ignoreStepResult != null) {
        testStep.setIgnoreStepResult(ignoreStepResult);
      }
      this.updateDetails(testStep);
    }
  }


  public List<TestStep> findAllByTestCaseIdAndIdIn(Long testCaseId, List<Long> stepIds) {
    return this.repository.findAllByTestCaseIdAndIdInOrderByPosition(testCaseId, stepIds);
  }

  public void updateTestDataParameterName(Long testDataId, String parameter, String newParameterName) {
    this.repository.updateTopLevelTestDataParameter(newParameterName, parameter, testDataId);
    List<TestStep> topConditionalSteps = this.repository.getTopLevelConditionalStepsExceptLoop(testDataId);
    for (TestStep step : topConditionalSteps) {
      updateChildLoops(step.getId(), parameter, newParameterName);
    }
    List<TestStep> loopSteps = this.repository.getAllLoopSteps(testDataId);
    for (TestStep step : loopSteps) {
      updateChildLoops(step.getId(), parameter, newParameterName);
    }
  }

  public void updateElementName(String oldName, String newName) {
    this.repository.updateElementName(newName, oldName);
  }

  private void updateChildLoops(Long parentId, String parameter, String newParameterName) {
    this.repository.updateChildStepsTestDataParameter(newParameterName, parameter, parentId);
    List<TestStep> conditionalSteps = this.repository.getChildConditionalStepsExceptLoop(parentId);
    for (TestStep step : conditionalSteps) {
      updateChildLoops(step.getId(), parameter, newParameterName);
    }
  }

  public List<TestStep> findAllByTestCaseIdAndNaturalTextActionIds(Long id, List<Integer> ids) {
    return this.repository.findAllByTestCaseIdAndNaturalTextActionIdIn(id, ids);
  }

  public Integer countAllByAddonActionIdIn(List<Long> ids) {
    return this.repository.countAllByAddonActionIdIn(ids);
  }

  public void updateAddonElementsName(String oldName, String newName) {
    List<TestStep> testSteps = this.repository.findAddonElementsByName(oldName);
    testSteps.forEach(testStep -> {
      Map<String, AddonElementData> elements = testStep.getAddonElements();
      for (Map.Entry<String, AddonElementData> entry : elements.entrySet()) {
        if (entry.getValue().getName().equals(oldName)) {
          entry.getValue().setName(newName);
        }
      }
      testStep.setAddonElements(elements);
      this.repository.save(testStep);
    });
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestStepEnabled()) return;
    log.debug("backup process for test step initiated");
    writeXML("test_steps", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for test step completed");
  }

  public Specification<TestStep> getExportXmlSpecification(BackupDTO backupDTO) {
    List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
    List<Long> testcaseIds = testCaseList.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("testCaseId", SearchOperation.IN, testcaseIds);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestStepSpecificationsBuilder testStepSpecificationsBuilder = new TestStepSpecificationsBuilder();
    testStepSpecificationsBuilder.params = params;
    return testStepSpecificationsBuilder.build();
  }

  @Override
  protected List<TestStepXMLDTO> mapToXMLDTOList(List<TestStep> list) {
    return exportTestStepMapper.mapTestSteps(list);
  }

  public void publishEvent(TestStep testSuite, EventType eventType) {
    TestStepEvent<TestStep> event = createEvent(testSuite, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestStepEvent<TestStep> createEvent(TestStep testSuite, EventType eventType) {
    TestStepEvent<TestStep> event = new TestStepEvent<>();
    event.setEventData(testSuite);
    event.setEventType(eventType);
    return event;
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsTestStepEnabled()) return;
    log.debug("import process for Test step initiated");
    importFiles("test_steps", importDTO);
    log.debug("import process for Test step completed");
  }

  @Override
  public List<TestStep> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
    if (importDTO.getIsCloudImport()) {
      List<TestStep> steps = mapper.mapTestStepsCloudList(xmlMapper.readValue(xmlData, new TypeReference<List<TestStepCloudXMLDTO>>() {
      }));
      List<TestStep> stepsCopy = new ArrayList<TestStep>(steps);
      for (TestStep step : stepsCopy) {
        if (step.getAddonActionId() != null && step.getAddonActionId() > 0) {
          try {
            addonNaturalTextActionService.findById(step.getAddonActionId());
          } catch (Exception e) {
            log.error(e.getMessage());
            steps.remove(step);
            log.info("deleting addon test step to avoid further issues, since addon is not installed");
          }
        }
        if (step.getType()==TestStepType.CUSTOM_FUNCTION){
          steps.remove(step);
          log.info("deleting Custom function test step to avoid further issues, since CSF are deprecated");
        }
      }
      return steps;
    } else {
      return mapper.mapTestStepsList(xmlMapper.readValue(xmlData, new TypeReference<List<TestStepXMLDTO>>() {
      }));
    }
  }


  @Override
  public Optional<TestStep> findImportedEntity(TestStep testStep, BackupDTO importDTO) {
    Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, testStep.getTestCaseId());
    if (testCase.isEmpty()) {
      return Optional.empty();
    }
    Optional<TestStep> previous = repository.findByTestCaseIdInAndImportedId(List.of(testCase.get().getId()), testStep.getId());
    return previous;
  }

  @Override
  public TestStep processBeforeSave(Optional<TestStep> previous, TestStep present, TestStep toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    setTemplateId(present, importDTO);
    processTestDataMap(present, importDTO);
    Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, present.getTestCaseId());
    if (testCase.isPresent())
      present.setTestCaseId(testCase.get().getId());
    if (present.getStepGroupId() != null) {
      Optional<TestCase> testComponent = testCaseService.getRecentImportedEntity(importDTO, present.getStepGroupId());
      if (testComponent.isPresent())
        present.setStepGroupId(testComponent.get().getId());
    }
    if (present.getParentId() != null) {
      Optional<TestStep> testStep = getRecentImportedEntity(importDTO, present.getParentId());
      if (testStep.isPresent()){
        present.setParentId(testStep.get().getId());
        if(testStep.get().getDisabled()){
          present.setDisabled(true);
        }
      }

    }

    return present;
  }

  public boolean hasToSkip(TestStep testStep, BackupDTO importDTO) {
    Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, testStep.getTestCaseId());
    return testCase.isEmpty();
  }

  @Override
  void updateImportedId(TestStep testStep, TestStep previous, BackupDTO importDTO) {
    previous.setImportedId(testStep.getId());
    save(previous);
  }

  private void processTestDataMap(TestStep present, BackupDTO importDTO) {
    TestStepDataMap testStepDataMap = present.getDataMapBean();
    if (testStepDataMap != null) {

      if ((testStepDataMap.getForLoop() != null || testStepDataMap.getWhileCondition() != null)
              && testStepDataMap.getForLoop() != null && testStepDataMap.getForLoop().getTestDataId() != null) {
        Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, testStepDataMap.getForLoop().getTestDataId());
        if (testData.isPresent())
          testStepDataMap.getForLoop().setTestDataId(testData.get().getId());
      }
    }

  }

  private void setTemplateId(TestStep present, BackupDTO importDTO) {
    if (!importDTO.getIsSameApplicationType() && present.getNaturalTextActionId() != null && present.getNaturalTextActionId() > 0) {
      NaturalTextActions nlpTemplate = naturalTextActionsService.findById(present.getNaturalTextActionId().longValue());
      try {
        if (importDTO.getWorkspaceType().equals(WorkspaceType.WebApplication)) {
          present.setNaturalTextActionId(nlpTemplate.getImportToWeb().intValue());
          if (nlpTemplate.getImportToWeb().intValue() == 0) {
            present.setDisabled(true);
          }
        } else if (importDTO.getWorkspaceType().equals(WorkspaceType.MobileWeb)) {
          present.setNaturalTextActionId(nlpTemplate.getImportToMobileWeb().intValue());
          if (nlpTemplate.getImportToMobileWeb().intValue() == 0) {
            present.setDisabled(true);
          }
        } else if (importDTO.getWorkspaceType().equals(WorkspaceType.AndroidNative)) {
          present.setNaturalTextActionId(nlpTemplate.getImportToAndroidNative().intValue());
          if (nlpTemplate.getImportToAndroidNative().intValue() == 0) {
            present.setDisabled(true);
          }
        } else if (importDTO.getWorkspaceType().equals(WorkspaceType.IOSNative)) {
          present.setNaturalTextActionId(nlpTemplate.getImportToIosNative().intValue());
          if (nlpTemplate.getImportToIosNative().intValue() == 0) {
            present.setDisabled(true);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        log.debug("mapping failed for templateId   " + present.getNaturalTextActionId().longValue());
        present.setNaturalTextActionId(0);
        present.setDisabled(true);
      }
    }
  }

  @Override
  public TestStep copyTo(TestStep testStep) {
    return mapper.copy(testStep);
  }

  @Override
  public TestStep save(TestStep testStep) {
    return repository.save(testStep);
  }

  @Override
  public Optional<TestStep> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    List<Long> testcaseIds = new ArrayList<>();
    testCaseService.findAllByWorkspaceVersionId(importDTO.getWorkspaceVersionId()).stream().forEach(testCase -> testcaseIds.add(testCase.getId()));
    Optional<TestStep> previous = repository.findByTestCaseIdInAndImportedId(testcaseIds, importedId);
    return previous;

  }

  public Optional<TestStep> findImportedEntityHavingSameName(Optional<TestStep> previous, TestStep current, BackupDTO importDTO) {
    return previous;
  }

  public boolean hasImportedId(Optional<TestStep> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestStep> previous, TestStep current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  public List<TestStep> findAllByTestCaseIdIn(List<Long> testCaseIds) {
    return this.repository.findAllByTestCaseIdInOrderByPositionAsc(testCaseIds);
  }
}
