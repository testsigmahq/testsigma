/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestStepService extends XMLExportService<TestStep> {
  private final TestStepRepository repository;
  private final RestStepService restStepService;
  private final RestStepMapper mapper;
  private final KibbutzService kibbutzService;
  private final AddonNaturalTextActionService addonNaturalTextActionService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final TestCaseService testCaseService;
  private final TestStepMapper exportTestStepMapper;

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

  public List<String> findKibbutzActionElementsByTestCaseIds(List<Long> testCaseIds) {
    List<String> elementsNames = new ArrayList<>();
    List<TestStep> testSteps = repository.findAllByTestCaseIdInAndAddonActionIdIsNotNull(testCaseIds);
    for (TestStep step : testSteps) {
      Map<String, KibbutzElementData> kibbutzElementData = step.getKibbutzElements();

      for (KibbutzElementData elementData : kibbutzElementData.values()) {
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
      kibbutzService.notifyActionNotUsing(addonNaturalTextAction);
    }
    publishEvent(testStep, EventType.DELETE);
  }

  public TestStep find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestStep missing with id:" + id));
  }

  public TestStep update(TestStep testStep) {
    RestStep restStep = testStep.getRestStep();
    testStep.setRestStep(null);
    testStep = this.repository.save(testStep);
    if (restStep != null) {
      restStep.setStepId(testStep.getId());
      restStep = this.restStepService.update(restStep);
      testStep.setRestStep(restStep);
    }
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
      kibbutzService.notifyActionUsing(addonNaturalTextAction);
    }
    publishEvent(testStep, EventType.CREATE);
    return testStep;
  }

  public void bulkUpdateProperties(Long[] ids, TestStepPriority testStepPriority, Integer waitTime, Boolean disabled) {
    if (testStepPriority != null) {
      this.repository.bulkUpdateProperties(ids, testStepPriority.toString(), waitTime);
    } else {
      this.repository.bulkUpdateProperties(ids, null, waitTime);
    }
    if (disabled != null)
      this.bulkUpdateDisablePropertyAlone(ids, disabled);
  }

  private void bulkUpdateDisablePropertyAlone(Long[] ids, Boolean disabled) {
    List<TestStep> testSteps = this.repository.findAllByIdInOrderByPositionAsc(ids);
    for (TestStep testStep : testSteps) {
      if (testStep.getParentId() == null || (testStep.getParentId() != null && testStep.getParentStep().getDisabled() != true)) {
        testStep.setDisabled(disabled);
        this.update(testStep);
      }
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

  public void updateKibbutzElementsName(String oldName, String newName) {
    List<TestStep> testSteps = this.repository.findKibbutzElementsByName(oldName);
    testSteps.forEach(testStep -> {
      Map<String, KibbutzElementData> elements = testStep.getKibbutzElements();
      for (Map.Entry<String, KibbutzElementData> entry : elements.entrySet()) {
        if (entry.getValue().getName().equals(oldName)) {
          entry.getValue().setName(newName);
        }
      }
      testStep.setKibbutzElements(elements);
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
}
