/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.executions;

import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.entity.EnvironmentRunResult;
import com.testsigma.automator.entity.ResultConstant;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.sql.Timestamp;

@Log4j2
public abstract class AbstractTestPlanRunTask extends Thread {
  protected TestDeviceEntity environment;
  protected EnvironmentRunResult environmentRunResult;
  protected Long environmentResultId;
  protected String operationName;
  protected String requestId;
  protected HttpClient assetsHttpClient;
  protected HttpClient webHttpClient;

  public AbstractTestPlanRunTask(TestDeviceEntity environment, String requestId, HttpClient webHttpClient
    , HttpClient assetsHttpClient) {
    this.environmentResultId = environment.getEnvironmentResultId();
    this.environment = environment;
    this.environmentRunResult = new EnvironmentRunResult(environment.getId());
    this.webHttpClient = webHttpClient;
    this.assetsHttpClient = assetsHttpClient;
    this.operationName = getClass().getSimpleName();
    this.requestId = requestId;
  }

  protected void beforeExecute() throws AutomatorException {
    Thread.currentThread().setName(this.operationName + "(" + this.environmentResultId + ")");
    log.info("------------------------------------------------------------------------------------------------");
    log.info("Starting execution for environment ::: " + environment);
    environmentRunResult.setStartTime(new Timestamp(System.currentTimeMillis()));
    environmentRunResult.setAgentPickedOn(new Timestamp(System.currentTimeMillis()));
    environmentRunResult.setId(environmentResultId);
  }


  protected abstract void execute() throws Exception;

  protected void afterExecute() throws AutomatorException {
    environmentRunResult.setEndTime(new Timestamp(System.currentTimeMillis()));
    environmentRunResult.setSessionCompletedOn(new Timestamp(System.currentTimeMillis()));
    log.debug("Finished execution for environmentResultId ::: " + environmentResultId);
    log.info("------------------------------------------------------------------------------------------------");
  }

  protected void setFailureMessage(Exception ex) {
    log.error(ex.getMessage(), ex);
    environmentRunResult.setErrorCode(0);
    environmentRunResult.setResult(ResultConstant.FAILURE);
    environmentRunResult.setMessage(ex.getMessage());
  }

  @SneakyThrows
  @Override
  public void run() {
    try {
      ThreadContext.put("X-Request-Id", requestId);
      beforeExecute();
      execute();
    } catch (Exception ex) {
      setFailureMessage(ex);
    } finally {
      afterExecute();
    }
  }
}
