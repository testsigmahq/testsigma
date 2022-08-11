package com.testsigma.automator.runners;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.DriverAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.exceptions.NaturalActionException;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.StaleElementReferenceException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@AllArgsConstructor
public class ActionStepExecutor {
  private static final List<ErrorCodes> ERROR_CODES = Arrays.asList(
    ErrorCodes.UNREACHABLE_BROWSER,
    ErrorCodes.NO_SUCH_SESSION_EXCEPTION,
    ErrorCodes.GENERAL_EXCEPTION);
  private TestCaseStepEntity testCaseStepEntity;
  private TestCaseStepResult testCaseStepResult;
  private Map<String, String> envSettings;
  private TestCaseResult testCaseResult;

  public void execute() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException,
    AutomatorException, ClassNotFoundException, InstantiationException {
    Class<?> className = Class.forName(testCaseStepEntity.getSnippetClass());
    DriverAction snippet = (DriverAction) className.getDeclaredConstructor().newInstance();

    snippet.setDriver(DriverManager.getRemoteWebDriver());
    snippet.setTimeout(testCaseStepEntity.getWaitTime().longValue());
    snippet.setTestDataPropertiesEntityMap(testCaseStepEntity.getTestDataMap());
    snippet.setElementPropertiesEntityMap(testCaseStepEntity.getElementsMap());
    snippet.setAttributesMap(testCaseStepEntity.getAttributesMap());
    snippet.setGlobalElementTimeOut(testCaseStepResult.getTestPlanRunSettingEntity().getElementTimeOut().longValue());
    snippet.setRuntimeDataProvider(prepareRunTimeDataProvider());
    snippet.setEnvSettings(envSettings);
    snippet.setAdditionalData(testCaseStepEntity.getAdditionalData());

    snippet.setTestDataProfileEntity(new TestDataProfileEntity(testCaseStepEntity.getTestDataIndex(),
            testCaseStepEntity.getSetName(), testCaseStepEntity.getTestDataProfileName()));
    snippet.setTestDataParameterName(testCaseStepEntity.getStepDetails().getDataMap().getTestData());
    ActionResult snippetResult = snippet.run();
    //We retry test step execution on failure based on the exception type.
    snippetResult = reTrySnippetIfEligible(snippetResult, snippet, testCaseStepEntity, testCaseStepResult);
    testCaseStepResult.getMetadata().setSnippetResultMetadata(snippet.getResultMetadata());
    testCaseStepResult.getOutputData().putAll(snippet.getTestDataParams());
    if (snippetResult == ActionResult.FAILED) {
      log.error("Test case step FAILED....");
      NaturalActionException naturalActionException = new NaturalActionException(snippet.getErrorMessage(), snippet.getException(),
        snippet.getErrorCode().getErrorCode().intValue());
      testCaseStepResult.setWebDriverException(naturalActionException.getErrorStackTraceTruncated());
      testCaseStepResult.setErrorCode(snippet.getErrorCode().getErrorCode().intValue());
      testCaseStepResult.setMessage(snippet.getErrorMessage());
      markTestcaseAborted(testCaseResult, testCaseStepResult, snippet);
      testCaseStepResult.getMetadata().setLog(testCaseStepResult.getWebDriverException());
      throw naturalActionException; //We are throwing an InvocationTargetException to handle Auto Healing
    } else {
      testCaseStepResult.setMessage(snippet.getSuccessMessage());
    }
  }

  private ActionResult reTrySnippetIfEligible(ActionResult snippetResult, DriverAction snippet,
                                                  TestCaseStepEntity testCaseStepEntity,
                                                  TestCaseStepResult testCaseStepResult) throws AutomatorException {

    for (int i = 0; i < testCaseStepEntity.getNoOfRetriesOnStepFailure(); i++) {
      boolean reTryRequired = eligibleForReTry(snippetResult, snippet, testCaseStepEntity, testCaseStepResult);
      if (reTryRequired) {
        testCaseStepResult.setRetriedCount(testCaseStepResult.getRetriedCount() + 1);
        log.info("Snippet Retry Count - " + testCaseStepResult.getRetriedCount());
        snippetResult = snippet.run();
      } else {
        log.info("Snippet is not eligible for retry...continuing");
        break;
      }
    }

    return snippetResult;
  }

  private boolean eligibleForReTry(ActionResult snippetResult, DriverAction snippet, TestCaseStepEntity stepEntity,
                                   TestCaseStepResult stepResult) {
    if (snippetResult != ActionResult.FAILED) {
      return false;
    }
    if (stepResult.getRetriedCount() < stepEntity.getNoOfRetriesOnStepFailure() && snippet.getException() != null
      && snippet.getException().getCause() != null) {
      if (snippet.getException().getCause() instanceof StaleElementReferenceException) {
        log.info("Snippet is eligible for retry...retrying");
        return true;
      }
    }
    return false;
  }

  private RuntimeDataProvider prepareRunTimeDataProvider() {
    return new RuntimeDataProvider();
  }

  private void markTestcaseAborted(TestCaseResult testCaseResult, TestCaseStepResult result, DriverAction snippet) {
    boolean isInAbortedList = ERROR_CODES.stream().anyMatch(code -> snippet.getErrorCode().equals(code));
    if (isInAbortedList) {
      DriverManager.getDriverManager().setRestartDriverSession(Boolean.TRUE);
      result.setResult(ResultConstant.ABORTED);
      result.setSkipExe(true);
      result.setMessage(AutomatorMessages.MSG_STEP_MAJOR_STEP_FAILURE);
      testCaseResult.setResult(ResultConstant.ABORTED);
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_ABORTED);
      if(!testCaseStepEntity.getStepDetails().getIgnoreStepResult()) {
        testCaseResult.setResult(ResultConstant.ABORTED);
        testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_ABORTED);
      }
    } else {
      result.setResult(ResultConstant.FAILURE);
    }
  }
}
