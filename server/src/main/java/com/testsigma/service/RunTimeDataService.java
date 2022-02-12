/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.constants.MessageConstants;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.RunTimeData;
import com.testsigma.model.TestDeviceResult;
import com.testsigma.repository.RunTimeDataRepository;
import com.testsigma.web.request.RuntimeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RunTimeDataService {
  private final RunTimeDataRepository repository;
  private final TestDeviceResultService testDeviceResultService;

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
}
