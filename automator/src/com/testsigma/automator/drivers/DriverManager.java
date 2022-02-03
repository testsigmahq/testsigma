package com.testsigma.automator.drivers;

import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.constants.SessionErrorType;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.runners.EnvironmentRunner;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import com.testsigma.automator.utilities.TimeUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.time.Instant;

@Log4j2
@Data
public abstract class DriverManager {
  public static final String MSG_LAB_MINUTES_EXCEEDED = "Allowed test execution duration on cloud devices/machines exceeded.";
  public static final String MSG_OS_NOT_SUPPORTED = "Selected device OS and version combination is no longer supported. Please edit the device and choose a supported OS and Version.";
  public static final String MSG_BROWSER_NOT_SUPPORTED = "Selected browser and version combination is no longer supported. Please edit the device and choose a supported browser and Version.";

  static private ThreadLocal<DriverManager> _driverManager = new ThreadLocal<>();
  private final TestDeviceSettings settings;
  private TestDeviceEntity testDeviceEntity;
  private TestDeviceSettings testDeviceSettings;
  private ExecutionLabType executionLabType;
  private String executionUuid;
  private TestsigmaDriver driver;
  private Boolean restartDriverSession = Boolean.FALSE;
  private Instant sessionStartInstant;
  private Instant sessionEndInstant;
  private String initialSessionId;
  private String restartSessionId;
  private Boolean isRestart = Boolean.FALSE;

  DriverManager() {
    this.executionUuid = EnvironmentRunner.getRunnerExecutionId();
    this.testDeviceEntity = EnvironmentRunner.getRunnerEnvironmentEntity();
    this.executionLabType = testDeviceEntity.getExecutionLabType();
    this.settings = testDeviceEntity.getEnvSettings();
    this.testDeviceSettings = testDeviceEntity.getEnvSettings();
    TestPlanRunSettingEntity executionSettings = testDeviceEntity.getTestPlanSettings();
    this.testDeviceSettings.setElementTimeout(executionSettings.getPageTimeOut());
    this.testDeviceSettings.setPageLoadTimeout(executionSettings.getElementTimeOut());
  }

  public static DriverManager getDriverManager() {
    return _driverManager.get();
  }

  public static void setDriverManager(DriverManager driverManager) {
    _driverManager.set(driverManager);
  }

  public static void getDriverManager(TestDeviceEntity testDeviceEntity,
                                      WorkspaceType workspaceType, Platform os, Long environmentResultId,
                                      DriverSessionType driverSessionType) throws AutomatorException {
    DriverManager driverManager = null;
    switch (workspaceType) {
      case WebApplication:
        driverManager = new WebDriverManager();
        break;
      case MobileWeb:
        if (os.equals(Platform.Android)) {
          driverManager = new AndroidWebDriverManager();
        } else {
          driverManager = new IOSWebDriverManager();
        }
        break;
      case AndroidNative:
        driverManager = new AndroidNativeDriverManager();
        break;
      case IOSNative:
        driverManager = new IOSNativeDriverManager();
        break;
      default:
        break;
    }
    if (driverManager != null) {
      DriverManager.setDriverManager(driverManager);
      driverManager.startSession(driverSessionType,
        environmentResultId, Boolean.FALSE);
    }
  }

  public static RemoteWebDriver getRemoteWebDriver() {
    RemoteWebDriver driver = null;
    if (DriverManager.getDriverManager().getDriver() != null) {
      driver = DriverManager.getDriverManager().getDriver().getRemoteWebDriver();
    }
    return driver;
  }

  public boolean isRestart() {
    return this.isRestart;
  }

  public abstract void performCleanUpAction(OnAbortedAction actionType) throws AutomatorException;

  protected abstract RemoteWebDriver createDriverSession() throws AutomatorException, IOException;

  protected void beforeSessionCreateActions() throws AutomatorException {
    log.debug("Executing before create session actions for execution UUID - " + executionUuid);
    sessionStartInstant = Instant.now();
  }

  protected void afterSessionCreateActions()
    throws AutomatorException {
    log.debug("Executing after create session actions for execution UUID - " + executionUuid);
  }

  public void startSession(DriverSessionType driverSessionType, Long entityId, Boolean isRestart) throws AutomatorException {
    RemoteWebDriver remoteWebDriver;
    beforeSessionCreateActions();

    try {
      remoteWebDriver = createDriverSession();
      storeSessionId(driverSessionType, entityId);
      this.isRestart = isRestart;
      if (!isRestart) {
        this.initialSessionId = getSessionId();
        log.info("Initial Session ID:" + this.initialSessionId);
      } else {
        this.restartSessionId = getSessionId();
        log.info("Restarted Session ID:" + this.restartSessionId);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      String errorMessage = parseErrorMessage(e.getMessage());
      if (StringUtils.isBlank(errorMessage)) {
        errorMessage = AutomatorMessages.EXCEPTION_WEBDRIVER_NOTCREATED + " - " + e.getMessage();
      } else {
        errorMessage = "Unable to create a new Test Session due to unexpected failure(0x537). " + errorMessage;
      }
      endSession();
      throw new AutomatorException(ErrorCodes.DRIVER_NOT_CREATED, errorMessage);
    }

    if (remoteWebDriver != null) {
      log.info("Driver Session ID - " + getSessionId());
    } else {
      throw new AutomatorException(ErrorCodes.DRIVER_NOT_CREATED, AutomatorMessages.EXCEPTION_WEBDRIVER_NOTCREATED);
    }

    afterSessionCreateActions();
  }

  public void endSession() throws AutomatorException {
    try {
      if (getDriver() != null && (getDriver().getRemoteWebDriver() != null)) {
        log.info("Ending session(if exists) with execution UUID - " + executionUuid + " and session ID - "
          + getSessionId());
        RemoteWebDriver driver = getDriver().getRemoteWebDriver();
        try {
          beforeEndSessionActions();
          driver.quit();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          driver.quit();
        }
        afterEndSessionActions();
      } else {
        log.debug("There is no driver session with executionID - " + executionUuid);
      }
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  protected void beforeEndSessionActions() throws AutomatorException {
    log.debug("Executing before end session actions for execution UUID - " + executionUuid);
  }

  protected void afterEndSessionActions() throws AutomatorException {
    log.debug("Executing after end session actions for execution UUID - " + executionUuid);
    new RuntimeDataProvider().clearRunTimeData(executionUuid);
    getDriver().setRemoteWebDriver(null);
    setDriver(null);
    sessionEndInstant = Instant.now();
    log.info("Total session time - " + TimeUtil.getFormattedDuration(sessionStartInstant, sessionEndInstant));
  }

  private String parseErrorMessage(String errorMessage) {
    String parsedErrorMessage = "";
    try {
      String[] tokens;
      if (errorMessage != null) {
        tokens = errorMessage.split("Original error:");
        if (tokens.length > 1) {
          tokens = tokens[1].split("Build info: version:");
          if (tokens.length > 1) {
            parsedErrorMessage = tokens[0];
          }
        }
        if(errorMessage.contains(SessionErrorType.PLATFORM_OS_NOT_SUPPORTED.name())){
          parsedErrorMessage = MSG_OS_NOT_SUPPORTED;
        }else if(errorMessage.contains(SessionErrorType.PLATFORM_BROWSER_VERSION_NOT_SUPPORTED.name())){
          parsedErrorMessage = MSG_BROWSER_NOT_SUPPORTED;
        }else if(errorMessage.contains(SessionErrorType.LAB_MINUTES_EXCEEDED.name())){
          parsedErrorMessage = MSG_LAB_MINUTES_EXCEEDED;
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return parsedErrorMessage;
  }

  public void storeSessionId(DriverSessionType driverSessionType, Long entityId) throws AutomatorException {
    try {
      switch (driverSessionType) {
        case ENVIRONMENT_SESSION:
          storeEnvironmentSessionId(entityId);
          break;
        case TEST_SUITE_SESSION:
          storeTestSuiteSessionId(entityId);
          break;
        case TEST_CASE_SESSION:
          storeTestCaseSessionId(entityId);
          break;
        default:
          log.error("Unknown driver session type value provided - " + driverSessionType);
      }
    } catch (Exception e) {
      endSession();
      throw e;
    }
  }

  private void storeEnvironmentSessionId(Long entityId) throws AutomatorException {
    try {
      TestDeviceResultRequest testDeviceResultRequest = new TestDeviceResultRequest();
      testDeviceResultRequest.setId(entityId);
      testDeviceResultRequest.setSessionId(getSessionId());

      AutomatorConfig.getInstance().getAppBridge().updateEnvironmentResultData(testDeviceResultRequest);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private void storeTestSuiteSessionId(Long entityId) throws AutomatorException {
    try {
      TestSuiteResultRequest testSuiteResultRequest = new TestSuiteResultRequest();
      testSuiteResultRequest.setId(entityId);
      testSuiteResultRequest.setSessionId(getSessionId());

      AutomatorConfig.getInstance().getAppBridge().updateTestSuiteResultData(testSuiteResultRequest);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private void storeTestCaseSessionId(Long entityId) throws AutomatorException {
    try {
      TestCaseResultRequest testCaseResultRequest = new TestCaseResultRequest();
      testCaseResultRequest.setId(entityId);
      testCaseResultRequest.setSessionId(getSessionId());

      AutomatorConfig.getInstance().getAppBridge().updateTestCaseResultData(testCaseResultRequest);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private boolean isTestsigmaLabMobileExecution() {
    return (WorkspaceType.isMobileApp(testDeviceEntity.getWorkspaceType())) &&
      (testDeviceEntity.getExecutionLabType() == ExecutionLabType.TestsigmaLab);
  }


  public String getSessionId() {
    return getRemoteWebDriver().getSessionId().toString();
  }

  public String getOngoingSessionId() {
    return StringUtils.isNotBlank(restartSessionId) ? restartSessionId : initialSessionId;
  }
}
