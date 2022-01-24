package com.testsigma.automator.runners;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.actions.KibbutzAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.exceptions.NaturalActionException;
import com.testsigma.automator.utilities.PathUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
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
  private LinkedList<ElementPropertiesEntity> kibbutzElementPropertiesEntity;

  public AddonNaturalTextActionStepExecutor(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult testCaseStepResult,
                                           TestCaseResult testCaseResult,
                                           Map<String, String> envSettings) {
    this.testCaseStepEntity = testCaseStepEntity;
    this.testCaseStepResult = testCaseStepResult;
    this.testCaseResult = testCaseResult;
    this.envSettings = envSettings;
  }

  public void execute() throws IOException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, AutomatorException, NoSuchFieldException, NaturalActionException {
    KibbutzAction kibbutzAction = new KibbutzAction(testCaseStepEntity, testCaseStepResult, this.kibbutzElementPropertiesEntity, this.envSettings);
    ActionResult snippetResult = kibbutzAction.run();
    if (snippetResult == ActionResult.FAILED) {
      log.error("Test case step FAILED....");
      NaturalActionException nlpSnippetException;
      if(kibbutzAction.getException() != null){
        nlpSnippetException = new NaturalActionException(kibbutzAction.getErrorMessage(), kibbutzAction.getException(),
          kibbutzAction.getErrorCode().getErrorCode().intValue());
      } else {
        nlpSnippetException = new NaturalActionException(kibbutzAction.getErrorMessage());
      }
      testCaseStepResult.setWebDriverException(nlpSnippetException.getErrorStackTraceTruncated());
      testCaseStepResult.setErrorCode(kibbutzAction.getErrorCode().getErrorCode().intValue());
      testCaseStepResult.setMessage(kibbutzAction.getErrorMessage());
      markTestcaseAborted(testCaseResult, testCaseStepResult, kibbutzAction);
      testCaseStepResult.getMetadata().setLog(testCaseStepResult.getWebDriverException());
      throw nlpSnippetException; //We are throwing an InvocationTargetException to handle Auto Healing
    } else {
      testCaseStepResult.setMessage(kibbutzAction.getSuccessMessage());
    }
  }

  private void markTestcaseAborted(TestCaseResult testCaseResult, TestCaseStepResult result, KibbutzAction snippet) {
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
