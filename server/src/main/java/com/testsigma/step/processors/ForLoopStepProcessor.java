package com.testsigma.step.processors;

import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.dto.TestCaseStepEntityDTO;
import com.testsigma.dto.TestStepDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.TestDataSet;
import com.testsigma.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@Log4j2
public class ForLoopStepProcessor extends StepProcessor {
  public ForLoopStepProcessor(WebApplicationContext webApplicationContext, List<TestCaseStepEntityDTO> testCaseStepEntityDTOS,
                              WorkspaceType workspaceType, Map<String, Element> elementMap,
                              TestStepDTO testStepDTO, Long testPlanId, TestDataSet testDataSet,
                              Map<String, String> environmentParams, TestCaseEntityDTO testCaseEntityDTO,
                              String environmentParamSetName, String dataProfile, Map<Long, Integer> dataSetIndex) {
    super(webApplicationContext, testCaseStepEntityDTOS, workspaceType, elementMap, testStepDTO, testPlanId, testDataSet,
      environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile, dataSetIndex);
  }

  private void attachTestDataProfileStepId(List<TestStepDTO> testCaseStepEntityDTOS) {
    for (TestStepDTO testStepEntity : testCaseStepEntityDTOS){
      if (testStepEntity.getTestDataProfileStepId()!=null){
        Optional<TestStepDTO> TDPStepEntity = testCaseStepEntityDTOS.stream().filter(step -> Objects.equals(step.getForLoopTestDataId(), testStepEntity.getTestDataProfileStepId())).findFirst();
        TDPStepEntity.ifPresent(stepDTO -> testStepEntity.setTestDataProfileStepId(stepDTO.getId()));
      }
    }
  }

  public void processLoop(List<TestStepDTO> testStepDTOS, List<Long> loopIds)
    throws TestsigmaException, CloneNotSupportedException {
    if (testStepDTOS != null) {
      loadLoop(testStepDTO, testStepDTOS);
      this.attachTestDataProfileStepId(testStepDTOS);
    }

    Long testDataId = testStepDTO.getForLoopTestDataId();
    Integer start = testStepDTO.getForLoopStartIndex();
    Integer end = testStepDTO.getForLoopEndIndex();

    TestData testData = testDataProfileService.find(testDataId);

    List<TestCaseStepEntityDTO> entityList = new ArrayList<>();
    List<TestDataSet> dataBank = testData.getData();
    if ((dataBank != null) && dataBank.size() > 0) {
      start = (start.equals(LOOP_START)) ? 1 : start;
      end = (end.equals(LOOP_END)) ? dataBank.size() : end;

      if (testStepDTO.getTestStepDTOS() != null && testStepDTO.getTestStepDTOS().size() > 0) {
        for (int i = start - 1; i < end && i < dataBank.size(); i++) {
          TestStepDTO parentEntity = testStepDTO.clone();
          TestDataSet dataSet = dataBank.get(i);
          TestCaseStepEntityDTO iteEntity = new TestCaseStepEntityDTO(); //iterableEntity -- Iteration
          iteEntity.setId(parentEntity.getId());
          dataSetIndex.put(testStepDTO.getId(), i);
          for (int lcount = 0; lcount < parentEntity.getTestStepDTOS().size(); lcount++) {
            TestStepDTO loopChildEntity = parentEntity.getTestStepDTOS().get(lcount);

            if (loopChildEntity.getType() == com.testsigma.model.TestStepType.REST_STEP) {
              new RestStepProcessor(webApplicationContext, iteEntity.getTestCaseSteps(), workspaceType,
                elementMap, loopChildEntity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                      environmentParamSetName, dataProfile, dataSetIndex).process();
              continue;
            }

            TestCaseStepEntityDTO processedChildEntity = new StepProcessor(webApplicationContext, testCaseStepEntityDTOS,
                    workspaceType, elementMap, loopChildEntity, testPlanId, dataSet, environmentParameters,
              testCaseEntityDTO, environmentParamSetName, testData.getTestDataName(),dataSetIndex).processStep();

            if (loopChildEntity.getType() == TestStepType.FOR_LOOP) {
              loopIds.add(loopChildEntity.getId());
              new ForLoopStepProcessor(webApplicationContext, iteEntity.getTestCaseSteps(), workspaceType,
                elementMap, loopChildEntity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                      environmentParamSetName, dataProfile,dataSetIndex)
                .processLoop(testStepDTOS, loopIds);
              continue;
            }

            processedChildEntity.setParentId(loopChildEntity.getParentId());
            processedChildEntity.setTestCaseId(loopChildEntity.getTestCaseId());
            processedChildEntity.setConditionType(loopChildEntity.getConditionType());
            processedChildEntity.setPriority(loopChildEntity.getPriority());
            processedChildEntity.setPreRequisite(loopChildEntity.getPreRequisiteStepId());
            processedChildEntity.setType(loopChildEntity.getType());
            processedChildEntity.setStepGroupId(loopChildEntity.getStepGroupId());
            processedChildEntity.setPosition(loopChildEntity.getPosition());
            processedChildEntity.setTestDataProfileName(testData.getTestDataName());
            processedChildEntity.setVisualEnabled(loopChildEntity.getVisualEnabled());
            processedChildEntity.setIndex(i + 1);
            processedChildEntity.setTestDataIndex(i);
            parentEntity.setTestDataId(testDataId);
            parentEntity.setTestDataIndex(i);
            parentEntity.setSetName(dataSet.getName());

            for (TestStepDTO centity : loopChildEntity.getTestStepDTOS()) {
              List<TestCaseStepEntityDTO> stepGroupSpecialSteps = new ArrayList<>();

              //TODO: check logic for test step key Generation and recursive logic for step group generation
              if (loopIds.contains(centity.getParentId())) {
                continue;
              }

              if (centity.getType() == TestStepType.REST_STEP) {
                new RestStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType,
                  elementMap, centity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                        environmentParamSetName, dataProfile ,dataSetIndex).process();
                processedChildEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
                continue;
              }

              if (TestStepType.FOR_LOOP == centity.getType()) {
                loopIds.add(centity.getId());
                new ForLoopStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType,
                  elementMap, centity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                        environmentParamSetName, dataProfile,dataSetIndex)
                  .processLoop(loopChildEntity.getTestStepDTOS(), loopIds);
                processedChildEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
                continue;
              }

              TestCaseStepEntityDTO processedSubChildStepEntity = new StepProcessor(webApplicationContext, testCaseStepEntityDTOS,
                      workspaceType, elementMap, centity, testPlanId, dataSet, environmentParameters,
                testCaseEntityDTO, environmentParamSetName, testData.getTestDataName(),dataSetIndex).processStep();

              processedSubChildStepEntity.setParentId(centity.getParentId());
              processedSubChildStepEntity.setTestCaseId(centity.getTestCaseId());
              processedSubChildStepEntity.setConditionType(centity.getConditionType());
              processedSubChildStepEntity.setPriority(centity.getPriority());
              processedSubChildStepEntity.setPreRequisite(centity.getPreRequisiteStepId());
              processedSubChildStepEntity.setType(centity.getType());
              processedSubChildStepEntity.setStepGroupId(centity.getStepGroupId());
              processedChildEntity.getTestCaseSteps().add(processedSubChildStepEntity);
              processedChildEntity.setTestDataProfileName(testData.getTestDataName());
            }
            iteEntity.getTestCaseSteps().add(processedChildEntity);
          }

          iteEntity.setParentId(parentEntity.getParentId());
          iteEntity.setTestCaseId(parentEntity.getTestCaseId());
          iteEntity.setConditionType(parentEntity.getConditionType());
          iteEntity.setPriority(parentEntity.getPriority());
          iteEntity.setPreRequisite(parentEntity.getPreRequisiteStepId());
          iteEntity.setPosition(parentEntity.getId().intValue());
          iteEntity.setWaitTime(parentEntity.getWaitTime() == null ? 0 : parentEntity.getWaitTime());
          iteEntity.setIndex(i + 1);
          iteEntity.setIteration(dataSet.getName());
          iteEntity.setTestDataProfileName(testData.getTestDataName());
          iteEntity.setType(parentEntity.getType());
          iteEntity.setNaturalTextActionId(parentEntity.getNaturalTextActionId());
          populateStepDetails(testStepDTO, iteEntity);
          iteEntity.setAction(parentEntity.getAction());
          iteEntity.setVisualEnabled(parentEntity.getVisualEnabled());
          entityList.add(iteEntity);
        }
      }
      testCaseStepEntityDTOS.addAll(entityList);
    } else {
      String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
        MessageConstants.MSG_UNKNOWN_TEST_DATA_LOOP, testCaseEntityDTO.getTestCaseName());
      throw new TestsigmaException(errorMessage);
    }
  }
}
