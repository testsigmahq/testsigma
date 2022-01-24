package com.testsigma.automator.actions;

import com.testsigma.automator.service.KibbutzService;
import com.testsigma.sdk.annotation.RunTimeData;
import com.testsigma.automator.constants.KibbutzActionParameterType;
import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.exceptions.ElementNotDisplayedException;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;

@Data
@Log4j2
public class KibbutzAction{
  private static final int MESSAGE_MAX_SIZE = 500;
  protected Exception exception;
  @Getter
  protected String errorMessage;
  @Getter
  protected String successMessage;
  protected ErrorCodes errorCode = ErrorCodes.GENERIC_ERROR;
  private TestCaseStepEntity testCaseStepEntity;
  private TestCaseStepResult result;
  private Class<?> elementClass;
  private Class<?> testDataClass;
  private Class<?> loggerClass;
  private Class<?> runTimeDataClass;
  private Class<?> clazz;
  private Object instance;
  private KibbutzService kibbutzService;
  private String elementValue;
  private String by;
  private ElementPropertiesEntity elementPropertiesEntity;
  private Map<String, String> envSettings;
  private RuntimeDataProvider runtimeDataProvider;
  private LinkedList<ElementPropertiesEntity> kibbutzElementPropertiesEntity;


  public KibbutzAction(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult result,
                       LinkedList<ElementPropertiesEntity> kibbutzElementPropertiesEntity, Map<String, String> envSettings)

  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
    this.testCaseStepEntity = testCaseStepEntity;
    this.result = result;
    this.kibbutzService = KibbutzService.getInstance();
    this.kibbutzElementPropertiesEntity = kibbutzElementPropertiesEntity;
    this.envSettings = envSettings;
    this.loadClasses();
  }

  private void loadClasses() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    AddonNaturalTextActionEntity entity = testCaseStepEntity.getAddonNaturalTextActionEntity();
    String jarFilePath = kibbutzService.checkAndDownloadJar(entity.getClassPath(), entity.getModifiedHash());
    this.clazz = kibbutzService.loadJarClass(jarFilePath, entity.getFullyQualifiedName(), true);
    this.instance = clazz.getDeclaredConstructor().newInstance();
  }

  private static Field getField(Class clazz, String fieldName)
    throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Class superClass = clazz.getSuperclass();
      if (superClass == null) {
        throw e;
      } else {
        return getField(superClass, fieldName);
      }
    }
  }

  public ActionResult execute() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method executeMethod = clazz.getDeclaredMethod("execute");
    executeMethod.setAccessible(true);
    Object returnValue = executeMethod.invoke(instance);
    return ActionResult.valueOf(returnValue.toString());
  }

  @SneakyThrows
  public ActionResult run() {
    ActionResult snippetResult;
    try {
      beforeExecute();
      snippetResult = execute();
      if (snippetResult == ActionResult.SUCCESS)
        setSuccessMessage();
      else
        setErrorMessage();
      return snippetResult;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      setException(new Exception(ObjectUtils.defaultIfNull(e.getCause(), e)));
      String message = StringUtils.isBlank(e.getMessage()) ? e.getCause().getMessage() : e.getMessage();
      setErrorMessage(message);
      if (e.getCause() == null || !e.getCause().getClass().getName().equals("java.lang.AssertionError"))
        handleException((Exception) getException().getCause());
      log.info(ActionResult.FAILED + " - " + getErrorMessage());
      snippetResult = ActionResult.FAILED;
    } finally {
      afterExecute();
    }
    return snippetResult;
  }

  private void beforeExecute() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, AutomatorException {
    setDriver(null);
    setElements();
    setTestData();
    initRunTimeDataVariable();
    this.setRuntimeDataProvider(this.prepareRunTimeDataProvider());
  }

  private void afterExecute() throws NoSuchFieldException, IllegalAccessException, AutomatorException {
    saveRunTimeData();
    setKibbutzLogsInTestStep(instance);
  }

  protected void setSuccessMessage() throws NoSuchFieldException, IllegalAccessException {
    String msg = getSuccessMessageFromSnippet(instance);
    if (msg == null)
      this.successMessage = "Teststep executed successfully";
    else
      this.successMessage = StringUtils.abbreviate(msg, MESSAGE_MAX_SIZE);
  }

  protected void setErrorMessage() throws NoSuchFieldException, IllegalAccessException {
    String msg = getErrorMessageFromSnippet(instance);
    if (msg == null)
      this.errorMessage = "Teststep execution failed. No Additional message was available.";
    else
      this.errorMessage = StringUtils.abbreviate(msg, MESSAGE_MAX_SIZE);
  }

  private void setKibbutzLogsInTestStep(Object instance) throws NoSuchFieldException, IllegalAccessException {
    log.info("Saving Kibbutz Logs in Test Step Result");
    Field loggerField = getField(instance.getClass(), "logger");
    loggerField.setAccessible(true);
    Field valueField = getField(loggerField.get(instance).getClass(), "value");
    valueField.setAccessible(true);
    StringBuilder val = (StringBuilder) valueField.get(loggerField.get(instance));
    result.setKibbutzActionLogs(val.toString());
    log.info("Successfully saved logs::" + val);
  }

  public void setDriver(Object object) throws IllegalAccessException {
    if (object != null)
      FieldUtils.writeField(object, "driver", DriverManager.getRemoteWebDriver(), true);
    else
      FieldUtils.writeField(instance, "driver", DriverManager.getRemoteWebDriver(), true);
  }

  public void setElements() throws AutomatorException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    AddonNaturalTextActionEntity addonNaturalTextActionEntity = testCaseStepEntity.getAddonNaturalTextActionEntity();
    Map<String, ElementPropertiesEntity> elementsMap = testCaseStepEntity.getElementsMap();
    if (!elementsMap.isEmpty()) {
      for (AddonNaturalTextActionParameterEntity addonNaturalTextActionParameterEntity : addonNaturalTextActionEntity.getPluginParameters()) {
        if (addonNaturalTextActionParameterEntity.getType() == KibbutzActionParameterType.ELEMENT) {
          log.info("Setting Element for Kibbutz Plugin Action Step - " + addonNaturalTextActionParameterEntity);
          elementPropertiesEntity = elementsMap.get(addonNaturalTextActionParameterEntity.getReference());
          ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(elementPropertiesEntity.getFindByType(),
            elementPropertiesEntity.getLocatorValue());
          Object elementInstance = kibbutzService.getElementInstance(elementPropertiesEntity.getLocatorValue(),
            elementSearchCriteria.getBy());
          FieldUtils.writeField(instance, addonNaturalTextActionParameterEntity.getName(), elementInstance, true);
          setDriver(elementInstance);
          log.info("Setting element instance - " + elementInstance);
        }
      }
    }
  }

  public void setTestData() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    AddonNaturalTextActionEntity addonNaturalTextActionEntity = testCaseStepEntity.getAddonNaturalTextActionEntity();
    Map<String, TestDataPropertiesEntity> testDataMap = testCaseStepEntity.getTestDataMap();
    if (!testDataMap.isEmpty()) {
      for (AddonNaturalTextActionParameterEntity addonNaturalTextActionParameterEntity : addonNaturalTextActionEntity.getPluginParameters()) {
        if (addonNaturalTextActionParameterEntity.getType() == KibbutzActionParameterType.TEST_DATA) {
          log.info("Setting Test Data for Kibbutz Plugin Action Step - " + addonNaturalTextActionParameterEntity);
          Object testDataInstance = kibbutzService.getTestDataInstance(testDataMap.get(addonNaturalTextActionParameterEntity
            .getReference()).getTestDataValue());
          FieldUtils.writeField(instance, addonNaturalTextActionParameterEntity.getName(), testDataInstance, true);
          log.info("Setting test data instance - " + testDataInstance);
        }
      }
    }
  }

  private void initRunTimeDataVariable() throws AutomatorException {
    try {
      for (Field field : clazz.getDeclaredFields()) {
        RunTimeData runTimeData = field.getAnnotation(RunTimeData.class);
        if (runTimeData != null) {
          log.info("Initializing  Run Time Data for Kibbutz Plugin Action Step - " + runTimeData);
          Object runTimeDataInstance = kibbutzService.getRunTimeDataInstance();
          FieldUtils.writeField(instance, field.getName(), runTimeDataInstance, true);
          log.info("Setting run time data to the main instance - " + field.getName());
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private void saveRunTimeData() throws AutomatorException {
    log.info("Saving run time data and sending run time data to provider");
    try {
      for (Field field : clazz.getDeclaredFields()) {
        RunTimeData runTimeData = field.getAnnotation(RunTimeData.class);
        if (runTimeData != null) {
          Field runTimeField = getField(instance.getClass(), field.getName());
          runTimeField.setAccessible(true);
          if (runTimeField != null && runTimeField.get(instance) != null) {
            Field valueField = getField(runTimeField.get(instance).getClass(), "value");
            valueField.setAccessible(true);
            String value = (String) valueField.get(runTimeField.get(instance));

            Field variableNameField = getField(runTimeField.get(instance).getClass(), "key");
            variableNameField.setAccessible(true);
            String variableName = (String) variableNameField.get(runTimeField.get(instance));

            if (variableName != null) {
              runtimeDataProvider.storeRuntimeVariable(variableName, value);
              log.info("Setting run time data to RunTimeData Provider - " + field.getName());
            } else {
              log.info("Skipping run time data to RunTimeData Provider - as the variable name is empty" + field.getName());
            }
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private String getSuccessMessageFromSnippet(Object instance) throws NoSuchFieldException, IllegalAccessException {
    Field valueField = getField(instance.getClass(), "successMessage");
    valueField.setAccessible(true);
    return (String) valueField.get(instance);
  }

  private String getErrorMessageFromSnippet(Object instance) throws NoSuchFieldException, IllegalAccessException {
    Field valueField = getField(instance.getClass(), "errorMessage");
    valueField.setAccessible(true);
    return (String) valueField.get(instance);
  }

  private RuntimeDataProvider prepareRunTimeDataProvider() {
    return new RuntimeDataProvider();
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
    } else if (e instanceof NotFoundException) {
      handleNotFoundExceptionType(e);
    } else if (e instanceof InvalidElementStateException) {
      handleInvalidStateExceptionType(e);
    } else if (e instanceof StaleElementReferenceException) {
      handleStaleElementExceptionType(e);
    } else if (e instanceof UnhandledAlertException) {
      handleUnhandledAlertExceptionType(e);
    } else if (e instanceof JavascriptException) {
      handleJavaScriptException(e);
    } else if (e instanceof UnexpectedTagNameException) {
      handleUnExpectedTagNameException(e);
    } else if (e instanceof AutomatorException) {
      handleAutomatorException(e);
    } else if (e instanceof MoveTargetOutOfBoundsException) {
      handleMoveTargetOutOfBoundException(e);
    } else if (e instanceof NoSuchSessionException) {
      handleNoSuchSessionException(e);
    } else if (e instanceof SessionNotCreatedException || e instanceof UnreachableBrowserException) {
      handleNoSuchSessionException(e);
    } else {
      log.error("unhandled error occurred", e);
    }
  }

  private void handleNoSuchSessionException(Exception e) {
    setErrorCode(ErrorCodes.NO_SUCH_SESSION_EXCEPTION);
    setErrorMessage("The browser connection is lost. Either the browser is closed by the user or the connection is terminated.");
  }

  private void handleMoveTargetOutOfBoundException(Exception e) {
    String errorMessage = "The intended element is out of page view range. " +
      "Please scroll and make the element viewable and perform this action/step.";
    setErrorMessage(errorMessage);
    setErrorCode(ErrorCodes.MOVE_TARGET_OUT_OF_BOUND_EXCEPTION);
  }

  private void handleUnExpectedTagNameException(Exception e) {
    String errorMessage = e.getMessage().substring(0, e.getMessage().indexOf("\n"));
    errorMessage = (errorMessage != null) ? errorMessage : "Given locator/testdata is not pointing to an expected element type.";
    setErrorMessage(errorMessage);
    setErrorCode(ErrorCodes.UNEXPECTED_TAG_NAME_EXCEPTION);
  }

  private void handleJavaScriptException(Exception e) {
    setErrorMessage("Unable to execute Javascript - " + e.getMessage());
    setErrorCode(ErrorCodes.JAVA_SCRIPT_EXCEPTION);
  }

  private void handleAutomatorException(Exception e) {
    if (e instanceof ElementNotDisplayedException) {
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.ELEMENT_NOT_DISPLAYED);
    } else {
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.AUTOMATOR_EXCEPTION);
    }
  }

  private void handleUnhandledAlertExceptionType(Exception e) {
    //There are no sub types of this exception.
    String errorMessage = "There is an unhandled Alert in this page which is obstructing actions on the page/element. Alert Text:\"%s\"";
    String alertText = ((UnhandledAlertException) e).getAlertText();
    if (alertText == null) {
      alertText = e.getMessage();
      alertText = alertText.substring(alertText.indexOf("{") + 1, alertText.indexOf("}"));
      alertText = alertText.substring(alertText.indexOf(":") + 1);
    }
    setErrorMessage(String.format(errorMessage, alertText));
    setErrorCode(ErrorCodes.UNHANDLED_ALERT_EXCEPTION);
  }

  private boolean getElementSearchCriteria() {
    return elementPropertiesEntity.getFindByType() != null && elementPropertiesEntity.getLocatorValue() != null;
  }

  private void handleStaleElementExceptionType(Exception e) {
    if (getElementSearchCriteria()) {
      String errorMessage = "The element  <b>\"%s:%s\"</b> is removed and currently not present in the page due to dynamic updates on the element or the " +
        "page.-<a class=\"text-link\" href = \"https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers#problem7\" " +
        "target=\"_blank\">https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers#problem7</a>";
      setErrorMessage(String.format(errorMessage, elementPropertiesEntity.getFindByType(), elementPropertiesEntity.getLocatorValue()));
    } else {
      String errorMessage = "The element state matching with given criteria is changed due to dynamic updates on the element or the " +
        "page. For more details, please visit below documentation.<br>-<a class=\"text-link\" href = \"https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers#problem7\" " +
        "target=\"_blank\">https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers#problem7</a>";
      setErrorMessage(errorMessage);
    }
    setErrorCode(ErrorCodes.STALE_ELEMENT_EXCEPTION);

  }

  private void handleInvalidStateExceptionType(Exception e) {
    if (e instanceof ElementNotVisibleException) {
      String errorMessage;
      if (getElementSearchCriteria()) {
        errorMessage = String.format("Element may be present but not visible in current page. Please verify if the " +
            "element <b>\"%s:%s\"</b> is pointing to a displayed element.",
          elementPropertiesEntity.getFindByType(), elementPropertiesEntity.getLocatorValue());
      } else {
        errorMessage = "Element may be present but not visible in current page. Please verify the given element criteria is pointing to a displayed/visible element.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_NOT_VISIBLE);
    } else if (e instanceof ElementClickInterceptedException) {
      String errorMessage = "Element may be present but unable to perform action. The element may be overlapped " +
        "or obscured by some other page element such as a Dialog box or an Alert.<br>" +
        "If the element is not in view, please try executing step <b>\"Scroll to the element UI_IDENTIFIER into view\"</b></b>";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_CLICK_INTERCEPTED_EXCEPTION);
    } else if (e instanceof ElementNotSelectableException) {
      String errorMessage;
      if (getElementSearchCriteria()) {
        errorMessage = String.format("Element is present but it is not selectable. Please check if the select element " +
            "corresponding to locator <b>\"%s:%s\"</b> is enabled and interactable.",
          elementPropertiesEntity.getFindByType(), elementPropertiesEntity.getLocatorValue());
      } else {
        errorMessage = "Element is present but it is not selectable. Please verify if the select element for given criteria is enabled and selectable.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_NOT_SELECTABLE_EXCEPTION);
    } else {
      String errorMessage = "Cannot perform any action on the element. Though element may be present, it is in a non-interactive state.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.INVALID_ELEMENT_STATE_EXCEPTION);
    }
  }

  private void handleNotFoundExceptionType(Exception e) {
    if (e instanceof InvalidSelectorException) {
      String errorMessage;
      if (getElementSearchCriteria()) {
        errorMessage = String.format("Unable to find element with  element<b> \"%s:%s\" </b>. Please verify XPATH syntax."
          , this.by, this.elementValue);
      } else {
        errorMessage = "There is no element matching with given criteria/condition. Please verify the given the element syntax.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.INVALID_SELECTOR);
    } else if (e instanceof NoSuchElementException) {
      String errorMessage;
      if (getElementSearchCriteria()) {
        errorMessage = String.format("Element with <b>\"%s:%s\" </b> not found in current page.<br> Please visit below page for more details<br> <a class=\"text-link\"" +
            " href = \"http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers\" " +
            "target=\"_blank\">http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers</a>",
          elementPropertiesEntity.getFindByType(), elementPropertiesEntity.getLocatorValue());
      } else {
        errorMessage = "There is no element matching with given criteria.<br> Please visit below page for more details<br> <a class=\"text-link\"" +
          " href = \"http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers\" " +
          "target=\"_blank\">http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-ui-identifiers</a>";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_ELEMENT);
    } else if (e instanceof NoAlertPresentException) {
      String errorMessage = "There is no Alert present in this page.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_ALERT_PRESENT_EXCEPTION);
    } else if (e instanceof NoSuchContextException) {
      String errorMessage = "The context is not available. Please verify the context availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_CONTEXT_EXCEPTION);
    } else if (e instanceof NoSuchCookieException) {
      String errorMessage = "The Cookie is not available. Please verify the Cookie availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_COOKIE_EXCEPTION);
    } else if (e instanceof NoSuchFrameException) {
      String errorMessage = "The Frame is not available in current page. Please verify the frame availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_FRAME_EXCEPTION);
    } else if (e instanceof NoSuchWindowException) {
      String errorMessage = "The Window target is not available. Please verify the Window availability. Make sure window/tab is present while executing this test.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_WINDOW_EXCEPTION);
    } else {
      //Here we handle ParentException(NotFoundException), We should make sure all sub types are already handled before this.
      String errorMessage = "Cannot find element on this page";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NOT_FOUND_EXCEPTION);
    }
  }

}
