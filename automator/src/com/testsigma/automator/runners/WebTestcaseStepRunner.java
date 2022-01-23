package com.testsigma.automator.runners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.KibbutzActionParameterType;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.exceptions.NaturalActionException;
import com.testsigma.automator.suggestion.SuggestionRunner;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class WebTestcaseStepRunner extends TestcaseStepRunner {

  public static final String INVALID_RUNTIME_DATA = "No data available for runtime test data variable %s. Refer previous Test Steps in this Test Case or Test Steps in other Test Cases to know the variable names saved by using store(naturalText) action Test Steps. Go to https://testsigma.com/docs/test-data/types/runtime/ to know more about runtime test data.";

  private TestCaseStepEntity testcaseStep;
  private TestCaseStepResult testCaseStepResult;
  private TestCaseResult testCaseResult;
  private Map<String, String> envSettings;
  private TestPlanRunSettingEntity settings;
  private RuntimeDataProvider runtimeDataProvider;
  private ElementPropertiesEntity elementPropertiesEntity;
  private String oldUiIdentifierDefinition = "";
  private LinkedList<ElementPropertiesEntity> kibbutzElementPropertiesEntity = new LinkedList<>();

  public WebTestcaseStepRunner(WorkspaceType workspaceType, Platform os) {
    super(workspaceType, os);
  }


  protected void execute(Map<String, String> envSettings, TestCaseStepResult testCaseStepResult,
                         TestCaseStepEntity testcaseStep, TestCaseResult testCaseResult) throws AutomatorException {
    this.settings = EnvironmentRunner.getRunnerEnvironmentEntity().getTestPlanSettings();
    this.testcaseStep = testcaseStep;
    this.testCaseResult = testCaseResult;
    this.envSettings = envSettings;
    this.testCaseStepResult = testCaseStepResult;
    runtimeDataProvider = new RuntimeDataProvider();
    setInitialElementData();
    try {
      updateRuntimeValueInElement();
      updateRuntimeValueInTestData();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      this.testCaseStepResult.setResult(ResultConstant.FAILURE);
      this.testCaseStepResult.setMessage(e.getMessage());
      return;
    }

    execute();

    if (isAutomatorException() && settings.getHasSuggestionFeature() && testcaseStep.getAddonNaturalTextActionEntity() == null) {
      diagnoseStepFailure();
    }
  }

  private void setInitialElementData() {
    if (testcaseStep.getElementsMap().size() > 0) {
      AddonNaturalTextActionEntity kibbutzPluginNlpEntity = testcaseStep.getAddonNaturalTextActionEntity();
      if (kibbutzPluginNlpEntity != null) {
        Map<String, ElementPropertiesEntity> uiIdentifiersMap = testcaseStep.getElementsMap();
        for (AddonNaturalTextActionParameterEntity kibbutzPluginNlpParameterEntity : kibbutzPluginNlpEntity.getPluginParameters()) {
          if (kibbutzPluginNlpParameterEntity.getType() == KibbutzActionParameterType.ELEMENT) {
            this.kibbutzElementPropertiesEntity.add(uiIdentifiersMap.get(kibbutzPluginNlpParameterEntity.getReference()));
          }
        }
        elementPropertiesEntity = this.kibbutzElementPropertiesEntity.getFirst();
      } else {
        elementPropertiesEntity = testcaseStep.getElementsMap().get("element-locator");
      }
      if (elementPropertiesEntity != null) {
        oldUiIdentifierDefinition = elementPropertiesEntity.getLocatorValue();
      }


    }
  }


  protected boolean execute() {

    //TODO: IN all exception cases replace getMessage with custom message from Message constants file.
    try {
      callExecutor();
      testCaseStepResult.setResult(ResultConstant.SUCCESS);
      if (StringUtils.isNotBlank(testCaseResult.getMessage())) {
        //In this condition we would have already set the success message from snippet executions.
      } else {
        testCaseResult.setMessage(AutomatorMessages.MSG_STEP_SUCCESS);
      }
    } catch (NaturalActionException naturalActionException) {
      log.info("Snippet execution failed:" + naturalActionException.getMessage());
      //Any additional details specific to ActionSnippet executions can be added here
    } catch (IllegalAccessException e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setErrorCode(ErrorCodes.ILLEGAL_ACCESS);
      testCaseStepResult.setMessage(cause.getMessage());
      testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(cause));
      log.error(e, e);
    } catch (IllegalArgumentException e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;

      if (cause instanceof NumberFormatException) {
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        testCaseStepResult.setErrorCode(ErrorCodes.INVALID_ARGUMENT);
        testCaseStepResult.setMessage(AutomatorMessages.INVALID_NUMBER_ARGUMENT);
      } else {
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        testCaseStepResult.setErrorCode(ErrorCodes.INVALID_ARGUMENT);
        testCaseStepResult.setMessage(cause.getMessage());
      }
      testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(cause));
      log.error(e, e);
    } catch (InvocationTargetException e) {
      Exception ex = (Exception) e.getCause();

      if (ex instanceof AutomatorException) {
        AutomatorException cause = (AutomatorException) e.getCause();
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        testCaseStepResult.setErrorCode(cause.getErrorCode());
        testCaseStepResult.setMessage(cause.getMessage());
        testCaseStepResult.setWebDriverException(cause.getRootMsg());

      } else {
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Map<String, Object> metadata = mapper.convertValue(testcaseStep, Map.class);
        Map<String, Object> oldMetadata = mapper.convertValue(testCaseStepResult.getMetadata(), Map.class);
        oldMetadata.putAll(metadata);
        StepResultMetadataEntity metadataEntity = mapper.convertValue(oldMetadata, StepResultMetadataEntity.class);
        testCaseStepResult.setMetadata(metadataEntity);
        testCaseStepResult.getMetadata().setTestStep(testcaseStep);
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        testCaseStepResult.setErrorCode(ErrorCodes.INVOCATION_TARGET);
        testCaseStepResult.setMessage(cause.getMessage());
        testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(cause));
      }
      log.error(e, e);
    } catch (NoSuchMethodException e) {
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setErrorCode(ErrorCodes.INVALID_METHOD);
      testCaseStepResult.setMessage(AutomatorMessages.EXCEPTION_METHOD_NOT_FOUND);
      testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(e));
      log.error(e, e);
    } catch (SecurityException e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setErrorCode(ErrorCodes.INVALID_CREDENTIALS);
      testCaseStepResult.setMessage(cause.getMessage());
      testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(e));
      log.error(e, e);
    } catch (AutomatorException tex) {
      //Throwable cause = tex.getCause();
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setErrorCode(ErrorCodes.UNKNOWN_PROBLEM);
      testCaseStepResult.setMessage(tex.getMessage());
      testCaseStepResult.setWebDriverException(tex.getRootMsg());
      log.error(tex, tex);
    } catch (Exception e) {
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setErrorCode(ErrorCodes.UNKNOWN_PROBLEM);
      testCaseStepResult.setMessage(e.getMessage());
      testCaseStepResult.setRootMsg(ExceptionUtils.getStackTrace(e));
      log.error(e, e);
    }
    return false;
  }

  private void callExecutor() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException, AutomatorException,
    ClassNotFoundException, InstantiationException, IOException, NoSuchFieldException {
    if (testcaseStep.getAddonNaturalTextActionEntity() != null) {
      AddonNaturalTextActionStepExecutor addonNaturalTextActionStepExecutor = new AddonNaturalTextActionStepExecutor(
        testcaseStep, testCaseStepResult, testCaseResult, envSettings);

      addonNaturalTextActionStepExecutor.execute();
    } else {
      ActionStepExecutor actionSnippetStepExecutor = new ActionStepExecutor(testcaseStep, testCaseStepResult,
        envSettings, testCaseResult);
      actionSnippetStepExecutor.execute();
    }
  }

  @Override
  protected void onStepFailure(ExecutionLabType exeType, WorkspaceType workspaceType,
                               TestPlanRunSettingEntity settings) throws AutomatorException {
    if (workspaceType.equals(WorkspaceType.WebApplication)) {
      DriverManager manger = DriverManager.getDriverManager();
      manger.performCleanUpAction(settings.getOnAbortedAction());
    }
  }

  private void updateRuntimeValueInElement() {
    try {
      Map<String, ElementPropertiesEntity> elementsMap = testcaseStep.getElementsMap();
      for (ElementPropertiesEntity elementEntity : elementsMap.values()) {
        String locatorValue = elementEntity.getLocatorValue();
        if (StringUtils.isNotBlank(locatorValue)) {
          int count = 0;
          int maxCount = 3;
          int first = locatorValue.indexOf(NaturalTextActionConstants.restDataiRunStartPattern);
          int second = locatorValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern,
            locatorValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern) + 1);
          String result = locatorValue;
          while (first >= 0 && count < maxCount) {
            String data = locatorValue.substring(first + 2, second);
            data = data.trim();
            String parameter = runtimeDataProvider.getRuntimeData(data);

            result = result.replaceAll(NaturalTextActionConstants.restDataRunStartPattern + Pattern.quote(data)
              + NaturalTextActionConstants.restDatRunaEndPattern, Matcher.quoteReplacement(parameter));

            locatorValue = result;
            first = locatorValue.indexOf(NaturalTextActionConstants.restDataiRunStartPattern);
            second = locatorValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern,
              locatorValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern) + 1);
            count++;
          }
          elementEntity.setLocatorValue(locatorValue);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setMessage(e.getMessage());
    }
  }

  private void updateRuntimeValueInTestData() throws AutomatorException {
    String testDataValue = testcaseStep.getTestDataValue();
    try {
      if (!StringUtils.isBlank(testDataValue)) {
        int count = 0;
        int maxCount = 3;
        int first = testDataValue.indexOf(NaturalTextActionConstants.restDataiRunStartPattern);
        int second = testDataValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern,
          testDataValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern) + 1);
        String result = testDataValue;
        while (first >= 0 && count < maxCount) {
          String data = testDataValue.substring(first + 2, second);
          data = data.trim();
          String parameter = runtimeDataProvider.getRuntimeData(data);
          result = result.replaceAll(NaturalTextActionConstants.restDataRunStartPattern + Pattern.quote(data)
            + NaturalTextActionConstants.restDatRunaEndPattern, Matcher.quoteReplacement(parameter));

          testDataValue = result;
          first = testDataValue.indexOf(NaturalTextActionConstants.restDataiRunStartPattern);
          second = testDataValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern,
            testDataValue.indexOf(NaturalTextActionConstants.restDataiRunaEndPattern) + 1);
          count++;
        }
        testcaseStep.setTestDataValue(testDataValue);
      }
    } catch (Exception e) {
      log.error(e, e);
      testCaseStepResult.setResult(ResultConstant.FAILURE);
      testCaseStepResult.setMessage(String.format(INVALID_RUNTIME_DATA, testDataValue));
      throw e;
    }
  }

  private boolean isAutomatorException() {
    return (testCaseStepResult.getResult() == ResultConstant.FAILURE) && (testCaseStepResult.getErrorCode() != null)
      && (testCaseStepResult.getErrorCode() > 10000);
  }

  private void diagnoseStepFailure() {
    try {
      SuggestionRunner runner = new SuggestionRunner(testcaseStep, testCaseStepResult, settings, envSettings);
      runner.diagniseStep();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
