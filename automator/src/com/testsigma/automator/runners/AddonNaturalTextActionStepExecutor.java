package com.testsigma.automator.runners;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.AddonAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.exceptions.NaturalActionException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
public class AddonNaturalTextActionStepExecutor {
  private static final List<ErrorCodes> ERROR_CODES = Arrays.asList(
    ErrorCodes.UNREACHABLE_BROWSER,
    ErrorCodes.NO_SUCH_SESSION_EXCEPTION,
    ErrorCodes.GENERAL_EXCEPTION);
  private TestCaseStepEntity testCaseStepEntity;
  private TestCaseStepResult testCaseStepResult;
  private TestCaseResult testCaseResult;
  private URLClassLoader jarFileLoader;
  private Class<?> elementClass;
  private Class<?> testDataClass;
  private Class<?> loggerClass;
  private Class<?> runTimeDataClass;
  private Map<String, String> envSettings;
  private LinkedList<ElementPropertiesEntity> addonElementPropertiesEntity;

  public AddonNaturalTextActionStepExecutor(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult testCaseStepResult,
                                           TestCaseResult testCaseResult,
                                           Map<String, String> envSettings) {
    this.testCaseStepEntity = testCaseStepEntity;
    this.testCaseStepResult = testCaseStepResult;
    this.testCaseResult = testCaseResult;
    this.envSettings = envSettings;
  }

  public void execute() throws IOException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, AutomatorException, NoSuchFieldException, NaturalActionException {
    AddonAction addonAction = new AddonAction(testCaseStepEntity, testCaseStepResult, this.addonElementPropertiesEntity, this.envSettings);
    ActionResult snippetResult = addonAction.run();
    if (snippetResult == ActionResult.FAILED) {
      log.error("Test case step FAILED....");
      NaturalActionException actionException;
      if(addonAction.getException() != null){
        actionException = new NaturalActionException(addonAction.getErrorMessage(), addonAction.getException(),
          addonAction.getErrorCode().getErrorCode().intValue());
      } else {
        actionException = new NaturalActionException(addonAction.getErrorMessage());
      }
      testCaseStepResult.setWebDriverException(actionException.getErrorStackTraceTruncated());
      testCaseStepResult.setErrorCode(addonAction.getErrorCode().getErrorCode().intValue());
      testCaseStepResult.setMessage(addonAction.getErrorMessage());
      markTestcaseAborted(testCaseResult, testCaseStepResult, addonAction);
      testCaseStepResult.getMetadata().setLog(testCaseStepResult.getWebDriverException());
      throw actionException; //We are throwing an InvocationTargetException to handle Auto Healing
    } else {
      testCaseStepResult.setMessage(addonAction.getSuccessMessage());
    }
  }

  private void markTestcaseAborted(TestCaseResult testCaseResult, TestCaseStepResult result, AddonAction snippet) {
    boolean isInAbortedList = ERROR_CODES.stream().anyMatch(code -> snippet.getErrorCode().equals(code));
    if (isInAbortedList) {
      DriverManager.getDriverManager().setRestartDriverSession(Boolean.TRUE);
      result.setResult(ResultConstant.ABORTED);
      result.setSkipExe(true);
      result.setMessage(AutomatorMessages.MSG_STEP_MAJOR_STEP_FAILURE);
      testCaseResult.setResult(ResultConstant.ABORTED);
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_ABORTED);
    } else {
      result.setResult(ResultConstant.FAILURE);
    }
  }

}
