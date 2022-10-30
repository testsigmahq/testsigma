/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.constants.MessageConstants;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.*;
import com.testsigma.model.recorder.RunTimeVariableDTO;
import com.testsigma.repository.RunTimeDataRepository;
import com.testsigma.web.request.RuntimeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RunTimeDataService {
  private final RunTimeDataRepository repository;
  private final TestDeviceResultService testDeviceResultService;
  private final NaturalTextActionsService naturalTextActionsService;
  private final TestStepService testStepService;

  public RunTimeData create(RunTimeData runTimeData) {
    return this.repository.save(runTimeData);
  }

  public RunTimeData findByTestPlanRunIdAndSessionId(Long executionRunId, String sessionId) throws ResourceNotFoundException {
    return repository.findByTestPlanRunIdAndSessionId(executionRunId, sessionId)
      .orElseThrow(
        () -> new ResourceNotFoundException("Could not find resource with id:" + executionRunId));
  }

  public RunTimeData findByExecutionRunId(Long executionRunId) throws ResourceNotFoundException {
    return repository.findByTestPlanRunIdAndSessionIdIsNull(executionRunId)
      .orElseThrow(
        () -> new ResourceNotFoundException("Could not find resource with id:" + executionRunId));
  }

  public RunTimeData findBySessionId(String sessionId) throws ResourceNotFoundException {
    return repository.findBySessionId(sessionId)
      .orElseThrow(
        () -> new ResourceNotFoundException("Could not find resource with session id:" + sessionId));
  }

  public RunTimeData update(RunTimeData runTimeData) {
    return this.repository.save(runTimeData);
  }

  public String getRunTimeData(String variableName, Long environmentResultId, String sessionId)
    throws ResourceNotFoundException {
    try {
      RunTimeData runTimeData;
      TestDeviceResult testDeviceResult = testDeviceResultService.find(environmentResultId);
        runTimeData = findByExecutionRunId(testDeviceResult.getTestPlanResultId());
      return runTimeData.getData().getString(variableName);
    } catch (JSONException | ResourceNotFoundException exception) {
      ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(exception.getMessage());
      String errorMessage = MessageConstants.getMessage(MessageConstants.RUNTIME_DATA_VARIABLE_NOT_FOUND);
      resourceNotFoundException.setErrorCode(MessageConstants.RUNTIME_DATA_VARIABLE_NOT_FOUND);
      resourceNotFoundException.setMessage(errorMessage);
      log.error(exception.getMessage(), exception);
      throw exception;
    }
  }

  public void updateRunTimeData(Long environmentResultId, RuntimeRequest runtimeRequest) throws ResourceNotFoundException {
    RunTimeData runTimeData = new RunTimeData();
    TestDeviceResult testDeviceResult = testDeviceResultService.find(environmentResultId);
    runTimeData.setTestPlanRunId(testDeviceResult.getTestPlanResultId());

    try {
      runTimeData.setSessionId(null);
      runTimeData = findByExecutionRunId(testDeviceResult.getTestPlanResultId());
    } catch (ResourceNotFoundException exception) {
      log.error(exception.getMessage(), exception);
    }

    JSONObject data = new JSONObject();
    if (runTimeData.getData() != null) {
      data = runTimeData.getData();
    }
    data.put(runtimeRequest.getName(), runtimeRequest.getValue());
    runTimeData.setData(data);
    this.update(runTimeData);
  }

  public List<RunTimeVariableDTO> getAllRuntimeVariablesInVersion(Long applicationVersionId) {
    log.info("Fetching all runtime variables used in application version:"+applicationVersionId);
    List<RunTimeVariableDTO> runTimeVariableDTOS = new ArrayList<>();
    List<NaturalTextActions> storeTemplates = naturalTextActionsService.findAllByAction("store");
    List<Integer> storeTemplateIds = new ArrayList<>();
    storeTemplates.forEach(template->storeTemplateIds.add(template.getId().intValue()));
    log.info("Store Templates Ids:"+storeTemplates);
    List<TestStep> testStepsWithStoreNlp = testStepService.findAllByWorkspaceVersionIdAndNaturalTextActionId(applicationVersionId,storeTemplateIds);
    log.info("Test steps with Store NLPs, size:"+testStepsWithStoreNlp.size());
    List<TestStep> testSteps = testStepService.findAllRuntimeDataRestStep(applicationVersionId);
    log.info("REST API Test steps with runtime data, size:"+testSteps.size());

    testSteps.addAll(testStepsWithStoreNlp);
    for(TestStep testStep: testSteps){
      if(testStep.getType() == TestStepType.NLP_TEXT){
        runTimeVariableDTOS.addAll(getRunTimeVariableDTOsForNlpStep(testStep));
      }else if(testStep.getType() == TestStepType.REST_STEP){
        runTimeVariableDTOS.addAll(getRunTimeVariableDTOsForRestAPIStep(testStep));
      }
    }
    return runTimeVariableDTOS;
  }

  private List<RunTimeVariableDTO> getRunTimeVariableDTOsForRestAPIStep(TestStep testStep) {
    log.info("Fetching runtime variable name from testStep:"+testStep.getId());
    RestStep restStep = testStep.getRestStep();
    List<RunTimeVariableDTO> runTimeVariableDTOS = new ArrayList<>();
    if(restStep.getHeaderRuntimeData().keySet().size() >0){
      restStep.getHeaderRuntimeData().keySet().forEach(key->runTimeVariableDTOS.add(createRunTimeVariableDTO(testStep,key)));
    }
    if(restStep.getBodyRuntimeData().keySet().size() >0){
      restStep.getBodyRuntimeData().keySet().forEach(key->runTimeVariableDTOS.add(createRunTimeVariableDTO(testStep,key)));
    }
    return runTimeVariableDTOS;
  }

  private List<RunTimeVariableDTO> getRunTimeVariableDTOsForNlpStep(TestStep testStep) {
    String runTimeVariableName = null;
    log.info("Fetching runtime variable name from testStep:"+testStep.getId());
    List<RunTimeVariableDTO> runTimeVariableDTOS = new ArrayList<>();
    if(testStep.getAction().indexOf("Store current") != -1){
      runTimeVariableName = testStep.getDataMap().getAttribute();
    }else{
      TestStepNlpData testStepNlpData = testStep.getDataMap().getTestData().getOrDefault(NlpConstants.TESTSTEP_DATAMAP_KEY_TEST_DATA,null);
      runTimeVariableName = (testStepNlpData != null)?testStepNlpData.getValue():runTimeVariableName;
    }

    if(runTimeVariableName != null){
      runTimeVariableDTOS.add(createRunTimeVariableDTO(testStep,runTimeVariableName));
    }
    return runTimeVariableDTOS;
  }
}
