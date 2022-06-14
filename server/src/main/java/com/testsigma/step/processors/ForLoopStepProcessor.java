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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class ForLoopStepProcessor extends StepProcessor {
  public ForLoopStepProcessor(WebApplicationContext webApplicationContext, List<TestCaseStepEntityDTO> testCaseStepEntityDTOS,
                              WorkspaceType workspaceType, Map<String, Element> elementMap,
                              TestStepDTO testStepDTO, Long testPlanId, TestDataSet testDataSet,
                              Map<String, String> environmentParams, TestCaseEntityDTO testCaseEntityDTO,
                              String environmentParamSetName, String dataProfile) {
    super(webApplicationContext, testCaseStepEntityDTOS, workspaceType, elementMap, testStepDTO, testPlanId, testDataSet,
      environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile);
  }

  public void processLoop(List<TestStepDTO> testStepDTOS, List<Long> loopIds)
    throws TestsigmaException, CloneNotSupportedException {
    if (testStepDTOS != null) {
      loadLoop(testStepDTO, testStepDTOS, loopIds);
    }

    Long testDataId = testStepDTO.getForLoopTestDataId();
    Integer start = testStepDTO.getForLoopStartIndex();
    Integer end = testStepDTO.getForLoopEndIndex();

    TestData testData = testDataProfileService.find(testDataId);

    List<TestCaseStepEntityDTO> entityList = new ArrayList<>();
    List<TestDataSet> dataBank = testData.getData();
    if ((dataBank != null) && dataBank.size() > 0) {

      end = (end.equals(LOOP_END)) ? dataBank.size() : end;

      if (testStepDTO.getTestStepDTOS() != null && testStepDTO.getTestStepDTOS().size() > 0) {
        for (int i = start - 1; i < end && i < dataBank.size(); i++) {
          TestStepDTO entity = testStepDTO.clone();
          TestDataSet dataSet = dataBank.get(i);
          TestCaseStepEntityDTO iteEntity = new TestCaseStepEntityDTO();
          iteEntity.setId(entity.getId());

          for (int lcount = 0; lcount < entity.getTestStepDTOS().size(); lcount++) {
            TestStepDTO loopentity = entity.getTestStepDTOS().get(lcount);

            if (loopentity.getType() == com.testsigma.model.TestStepType.REST_STEP) {
              new RestStepProcessor(webApplicationContext, iteEntity.getTestCaseSteps(), workspaceType,
                elementMap, loopentity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                      environmentParamSetName, dataProfile).process();
              continue;
            }


            TestCaseStepEntityDTO exeEntity = new StepProcessor(webApplicationContext, testCaseStepEntityDTOS,
                    workspaceType, elementMap, loopentity, testPlanId, dataSet, environmentParameters,
              testCaseEntityDTO, environmentParamSetName, testData.getTestDataName()).processStep();
            if (loopentity.getType() == TestStepType.FOR_LOOP) {
              loopIds.add(loopentity.getId());
              new ForLoopStepProcessor(webApplicationContext, iteEntity.getTestCaseSteps(), workspaceType,
                elementMap, loopentity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                      environmentParamSetName, dataProfile)
                .processLoop(entity.getTestStepDTOS(), loopIds);
              continue;
            }

            exeEntity.setParentId(loopentity.getParentId());
            exeEntity.setTestCaseId(loopentity.getTestCaseId());
            exeEntity.setConditionType(loopentity.getConditionType());
            exeEntity.setPriority(loopentity.getPriority());
            exeEntity.setPreRequisite(loopentity.getPreRequisiteStepId());
            exeEntity.setType(loopentity.getType());
            exeEntity.setStepGroupId(loopentity.getStepGroupId());
            exeEntity.setPosition(loopentity.getPosition());
            exeEntity.setTestDataProfileName(testData.getTestDataName());
            exeEntity.setVisualEnabled(loopentity.getVisualEnabled());
            exeEntity.setIndex(i + 1);
            entity.setTestDataId(testDataId);
            entity.setTestDataIndex(i);
            entity.setSetName(dataSet.getName());

            for (TestStepDTO centity : loopentity.getTestStepDTOS()) {
              List<TestCaseStepEntityDTO> stepGroupSpecialSteps = new ArrayList<>();

              //TODO: check logic for test step key Generation and recursive logic for step group generation
              if (loopIds.contains(centity.getParentId())) {
                continue;
              }

              if (centity.getType() == TestStepType.REST_STEP) {
                new RestStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType,
                  elementMap, centity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                        environmentParamSetName, dataProfile).process();
                exeEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
                continue;
              }

              if (TestStepType.FOR_LOOP == centity.getType()) {
                loopIds.add(centity.getId());
                new ForLoopStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType,
                  elementMap, centity, testPlanId, dataSet, environmentParameters, testCaseEntityDTO,
                        environmentParamSetName, dataProfile)
                  .processLoop(loopentity.getTestStepDTOS(), loopIds);
                exeEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
                continue;
              }

              TestCaseStepEntityDTO cstepEntity = new StepProcessor(webApplicationContext, testCaseStepEntityDTOS,
                      workspaceType, elementMap, centity, testPlanId, dataSet, environmentParameters,
                testCaseEntityDTO, environmentParamSetName, testData.getTestDataName()).processStep();

              cstepEntity.setParentId(centity.getParentId());
              cstepEntity.setTestCaseId(centity.getTestCaseId());
              cstepEntity.setConditionType(centity.getConditionType());
              cstepEntity.setPriority(centity.getPriority());
              cstepEntity.setPreRequisite(centity.getPreRequisiteStepId());
              cstepEntity.setType(centity.getType());
              cstepEntity.setStepGroupId(centity.getStepGroupId());
              exeEntity.getTestCaseSteps().add(cstepEntity);
              exeEntity.setTestDataProfileName(testData.getTestDataName());
            }
            iteEntity.getTestCaseSteps().add(exeEntity);
          }

          iteEntity.setParentId(entity.getParentId());
          iteEntity.setTestCaseId(entity.getTestCaseId());
          iteEntity.setConditionType(entity.getConditionType());
          iteEntity.setPriority(entity.getPriority());
          iteEntity.setPreRequisite(entity.getPreRequisiteStepId());
          iteEntity.setPosition(entity.getId().intValue());
          iteEntity.setWaitTime(entity.getWaitTime() == null ? 0 : entity.getWaitTime());
          iteEntity.setIndex(i + 1);
          iteEntity.setIteration(dataSet.getName());
          iteEntity.setTestDataProfileName(testData.getTestDataName());
          iteEntity.setType(entity.getType());
          iteEntity.setNaturalTextActionId(entity.getNaturalTextActionId());
          populateStepDetails(testStepDTO, iteEntity);
          iteEntity.setAction(entity.getAction());
          iteEntity.setVisualEnabled(entity.getVisualEnabled());
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
