package com.testsigma.automator.runners;


import com.testsigma.automator.constants.ExecutionStatus;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriversUpdateService;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.exceptions.TestsigmaNoParallelRunException;
import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.utilities.ErrorUtil;
import com.testsigma.automator.utilities.PathUtil;
import com.testsigma.automator.utilities.ScreenCaptureUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Log4j2
@Data
public abstract class EnvironmentRunner {

  protected static final ThreadLocal<ExecutionStatus> executionStatus = new ThreadLocal<>();
  protected static final ThreadLocal<TestDeviceEntity> _runnerEnvironmentEntity = new ThreadLocal<>();
  protected static final ThreadLocal<EnvironmentRunResult> _runnerEnvironmentRunResult = new ThreadLocal<>();
  protected static final ThreadLocal<String> _runnerExecutionId = new ThreadLocal<>();
  protected static final ThreadLocal<HttpClient> _webAppHttpClient = new ThreadLocal<>();
  protected static final ThreadLocal<HttpClient> _assetsHttpClient = new ThreadLocal<>();

  protected TestDeviceEntity testDeviceEntity;
  protected EnvironmentRunResult environmentRunResult;
  protected String testPlanId;
  protected WorkspaceType workspaceType;


  public EnvironmentRunner(TestDeviceEntity testDeviceEntity, EnvironmentRunResult environmentRunResult, HttpClient webAppHttpClient,
                           HttpClient assetsHttpClient) {
    this.testDeviceEntity = testDeviceEntity;
    this.environmentRunResult = environmentRunResult;
    this.workspaceType = testDeviceEntity.getWorkspaceType();
    this.testPlanId = getTestPlanId();

    _webAppHttpClient.set(webAppHttpClient);
    _assetsHttpClient.set(assetsHttpClient);

    testDeviceEntity.getEnvSettings().setExecutionRunId(testDeviceEntity.getExecutionRunId());
    testDeviceEntity.getEnvSettings().setOs(this.getOs());
  }

  public static ExecutionStatus getExecutionStatus() {
    return executionStatus.get();
  }

  public static boolean isRunning() {
    return executionStatus.get() == ExecutionStatus.STARTED;
  }

  public static void setStartedStatus() {
    executionStatus.set(ExecutionStatus.STARTED);
  }

  public static void setStoppedStatus() {
    executionStatus.set(ExecutionStatus.STOPPED);
  }

  public static TestDeviceEntity getRunnerEnvironmentEntity() {
    return _runnerEnvironmentEntity.get();
  }

  public static void setRunnerEnvironmentEntity(TestDeviceEntity testDeviceEntity) {
    _runnerEnvironmentEntity.set(testDeviceEntity);
  }

  public static EnvironmentRunResult getRunnerEnvironmentRunResult() {
    return _runnerEnvironmentRunResult.get();
  }

  public static void setRunnerEnvironmentRunResult(EnvironmentRunResult environmentRunResult) {
    _runnerEnvironmentRunResult.set(environmentRunResult);
  }

  public static String getRunnerExecutionId() {
    return _runnerExecutionId.get();
  }

  public static void setRunnerExecutionId(String testPlanId) {
    _runnerExecutionId.set(testPlanId);
  }

  public static HttpClient getWebAppHttpClient() {
    return _webAppHttpClient.get();
  }

  public static HttpClient getAssetsHttpClient() {
    return _assetsHttpClient.get();
  }

  protected void beforeExecute() throws AutomatorException {
    checkForEmptyEnvironment();
    new ScreenCaptureUtil().createScreenshotsFolder();
    new ErrorUtil().checkError(testDeviceEntity.getErrorCode(), null);
    if (testDeviceEntity.getWorkspaceType()==WorkspaceType.WebApplication){
      new DriversUpdateService().syncBrowserDriver(testDeviceEntity);
    }
  }

  public EnvironmentRunResult run() {
    try {
      populateThreadContextData();
      setRunnerEnvironmentEntity(testDeviceEntity);
      setRunnerEnvironmentRunResult(environmentRunResult);
      setRunnerExecutionId(testPlanId);
      beforeExecute();
      setStartedStatus();
      execute();
      afterExecute();
      setEnvironmentResult();
      setStoppedStatus();
    } catch (TestsigmaNoParallelRunException e){
      environmentRunResult.setResult(ResultConstant.STOPPED);
      environmentRunResult.setErrorCode(e.getErrorCode());
      environmentRunResult.setMessage(e.getMessage());
    } catch (AutomatorException e) {
      environmentRunResult.setResult(ResultConstant.NOT_EXECUTED);
      environmentRunResult.setErrorCode(e.getErrorCode());
      environmentRunResult.setMessage(e.getDispMessage());
      log.info("Test Engine Exception in TestSuiteDriver - " + environmentRunResult.getMessage() + " - " + environmentRunResult.getErrorCode());
      log.error(e.getMessage(), e);
    } finally {
      deleteFolder();
    }
    return environmentRunResult;
  }

  private void populateThreadContextData() {
    ThreadContext.put("TEST_DEVICE_RESULT", environmentRunResult.getId() + "");
    ThreadContext.put("TEST_PLAN", testDeviceEntity.getTestPlanId() + "");
    ThreadContext.put("TEST_PLAN_RESULT", testDeviceEntity.getExecutionRunId() + "");
  }

  protected void setEnvironmentResult() {
    if (!isRunning()) {
      environmentRunResult.setResult(ResultConstant.STOPPED);
      environmentRunResult.setMessage(AutomatorMessages.MSG_USER_ABORTED_EXECUTION);
      environmentRunResult.setErrorCode(com.testsigma.automator.constants.ErrorCodes.USER_STOPPED_EXECUTION);
    } else {
      environmentRunResult.setMessage(AutomatorMessages.MSG_ENVIRONMENT_SUCCESS);
    }
  }

  protected void afterExecute() throws AutomatorException {
  }

  private Platform getOs() {
    return (testDeviceEntity.getEnvSettings().getPlatform() != null) ?
      testDeviceEntity.getEnvSettings().getPlatform() : null;
  }

  public void deleteFolder() {
    TestDeviceSettings envSettings = EnvironmentRunner.getRunnerEnvironmentEntity().getEnvSettings();
    try {
      String fullPath = Paths.get(PathUtil.getInstance().getUploadPath(), envSettings.getEnvRunId() + "")
        .toFile().getAbsolutePath();
      File file = new File(fullPath);
      if (file.exists()) {
        FileUtils.deleteDirectory(file);
      } else {
        log.info("Directory doesn't exist. Unable to delete - " + file.getAbsolutePath());
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  protected abstract void execute() throws AutomatorException;

  protected String getTestPlanId() {
    return String.format("%s-%s", environmentRunResult.getId(), testDeviceEntity.getId());
  }

  protected abstract void checkForEmptyEnvironment() throws AutomatorException;
}
