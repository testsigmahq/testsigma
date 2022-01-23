/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.entity.AttributePropertiesEntity;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Data
public abstract class Action {
  private static final int MESSAGE_MAX_SIZE = 500;
  protected Exception exception;
  protected Object actualValue;
  protected Long timeout = 30L;
  protected ErrorCodes errorCode = ErrorCodes.GENERIC_ERROR;
  protected Long globalElementTimeOut;
  protected Map<String, TestDataPropertiesEntity> testDataPropertiesEntityMap;
  protected Map<String, ElementPropertiesEntity> elementPropertiesEntityMap;
  protected Map<String, AttributePropertiesEntity> attributesMap;
  protected RuntimeDataProvider runtimeDataProvider;
  protected Map<Object, Object> resultMetadata = new HashMap<>();
  protected Map<String, String> testDataParams = new HashMap<>();
  protected Map<String, Object> additionalData = new HashMap<>();
  protected Map<String, String> envSettings = new HashMap<>();
  @Getter
  protected String errorMessage;
  @Getter
  protected String successMessage;

  public ActionResult run() throws AutomatorException {
    try {
      beforeExecute();
      execute();
      log.info(ActionResult.SUCCESS + " - " + getSuccessMessage());
      return ActionResult.SUCCESS;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      setException(new Exception(ObjectUtils.defaultIfNull(e.getCause(), e)));
      setErrorMessage("Unable to perform specified action, please verify element/Test data.");
      setErrorCode(ErrorCodes.GENERAL_EXCEPTION);
      handleException((Exception) getException().getCause());
      log.info(ActionResult.FAILED + " - " + getErrorMessage());
      return ActionResult.FAILED;
    } finally {
      try {
        afterExecute();
      } catch (NoSuchSessionException e) {
        log.error("Unable to reset implicit timeouts. Session is closed.", e);
      }
    }
  }

  protected void beforeExecute() throws AutomatorException {
    log.debug("Started executing " + this.getClass().getName());
  }

  protected abstract void execute() throws Exception;

  protected void afterExecute() throws AutomatorException {
    log.debug("Finished executing " + this.getClass().getName());
  }

  protected void setErrorMessage(String errorMessage) {
    this.errorMessage = StringUtils.abbreviate(errorMessage, MESSAGE_MAX_SIZE);
  }

  protected void setSuccessMessage(String successMessage) {
    this.successMessage = StringUtils.abbreviate(successMessage, MESSAGE_MAX_SIZE);
  }

  protected ElementPropertiesEntity getElementPropertiesEntity(String elementActionVarName) {
    return elementPropertiesEntityMap.get(elementActionVarName);
  }

  protected TestDataPropertiesEntity getTestDataPropertiesEntity(String testDataActionVariableName) {
    return testDataPropertiesEntityMap.get(testDataActionVariableName);
  }

  protected AttributePropertiesEntity getAttributePropertiesEntity(String actionAttributeVarName) {
    return attributesMap.get(actionAttributeVarName);
  }

  protected void handleException(Exception e) {
    log.error("Exception while executing Action - " + e.getMessage(), e);

    if (e instanceof TimeoutException) {
      setErrorMessage("Element is not available on the page or didn't load with in given wait time. This could " +
        "also be because of element being in a different iframe");
      setErrorCode(ErrorCodes.ELEMENT_TIMEOUT);
    } else if (e instanceof UnreachableBrowserException) {
      setErrorMessage("Unable to perform action. The Web Browser has been closed in a previous step or crashed " +
        "unexpectedly");
      setErrorCode(ErrorCodes.UNREACHABLE_BROWSER);
    } else if (e instanceof IllegalArgumentException) {
      //Assert errors will come here
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.ASSERT_ERROR);
    } else if (e instanceof SessionNotCreatedException || e instanceof UnreachableBrowserException) {
      setErrorMessage("Unable to perform specified action on current page - " + e.getMessage());
      setErrorCode(ErrorCodes.NO_SUCH_SESSION_EXCEPTION);
    } else if (e instanceof UnsupportedCommandException) {
      if (e.getMessage().contains("has already finished")) {
        setErrorMessage("Unable to perform specified action on current page - " + e.getMessage());
        setErrorCode(ErrorCodes.NO_SUCH_SESSION_EXCEPTION);
      }
    } else if (e instanceof WebDriverException) {
      setErrorMessage("Unable to perform specified action on current page - " + e.getMessage());
      setErrorCode(ErrorCodes.WEBDRIVER_EXCEPTION);
    } else {
      log.error("unhandled error occurred", e);
    }
  }
}
