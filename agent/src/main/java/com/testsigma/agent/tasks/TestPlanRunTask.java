package com.testsigma.agent.tasks;

import com.testsigma.agent.exception.DeviceNotConnectedException;
import com.testsigma.agent.exception.MobileLibraryInstallException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.AssetsHttpClient;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.MobileAutomationServerService;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.MobileDevice;
import com.testsigma.agent.mobile.ios.IosDeviceService;
import com.testsigma.agent.utils.PathUtil;
import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.entity.TestDeviceSettings;
import com.testsigma.automator.entity.WorkspaceType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.executions.AbstractTestPlanRunTask;
import com.testsigma.automator.runners.ExecutionEnvironmentRunner;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.context.WebApplicationContext;

@Log4j2
public class TestPlanRunTask extends AbstractTestPlanRunTask {

  protected MobileDevice mobileDevice;
  protected DeviceContainer deviceContainer;
  protected MobileAutomationServerService mobileAutomationServerService;
  protected IosDeviceService iosDeviceService;
  @Setter
  WebApplicationContext webApplicationContext;

  public TestPlanRunTask(TestDeviceEntity testDeviceEntity) {
    super(testDeviceEntity, ThreadContext.get("X-Request-Id"), new WebAppHttpClient(), new AssetsHttpClient());
  }

  @Override
  public void execute() throws Exception {
    ExecutionEnvironmentRunner driver = new ExecutionEnvironmentRunner(environment, environmentRunResult,
      webHttpClient, assetsHttpClient);
    environmentRunResult = driver.run();
  }

  @Override
  protected void beforeExecute() throws AutomatorException {
    this.deviceContainer = webApplicationContext.getBean(DeviceContainer.class);
    this.mobileAutomationServerService = webApplicationContext.getBean(MobileAutomationServerService.class);
    this.iosDeviceService = webApplicationContext.getBean(IosDeviceService.class);
    super.beforeExecute();
    if (WorkspaceType.isMobileApp(environment.getWorkspaceType())) {
      setupLocalDevice();
    }
  }

  @Override
  public void afterExecute() throws AutomatorException {
    super.afterExecute();
    AutomatorConfig.getInstance().getAppBridge().postEnvironmentResult(environmentRunResult);
  }

  protected void setupLocalDevice()
    throws AutomatorException {
    log.info("Setting up local mobile device");
    try {
      checkDeviceAvailability();
      TestDeviceSettings testDeviceSettings = environment.getEnvSettings();
      setAppiumUrl(testDeviceSettings);
      testDeviceSettings.setDeviceName(mobileDevice.getName());
      testDeviceSettings.setDeviceUniqueId(mobileDevice.getUniqueId());
      if (Platform.Android.equals(getEnvPlatform())) {
        testDeviceSettings.setChromedriverExecutableDir(PathUtil.getInstance().getDriversPath());
      } else if (Platform.iOS.equals(getEnvPlatform())) {
        iosDeviceService.setupWda(mobileDevice);
      }
      environment.setEnvSettings(testDeviceSettings);
      mobileAutomationServerService.installDrivers(this.mobileDevice.getOsName(), this.mobileDevice.getUniqueId());
    } catch (TestsigmaException | DeviceNotConnectedException | MobileLibraryInstallException e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private void checkDeviceAvailability() throws DeviceNotConnectedException, TestsigmaException {
    mobileDevice = deviceContainer.getDevice(environment.getAgentDeviceUuid());
    if (this.mobileDevice == null || !this.mobileDevice.getIsOnline()) {
      environmentRunResult.setErrorCode(ErrorCodes.DEVICE_NOT_FOUND);
      environmentRunResult.setMessage(AutomatorMessages.getMessage(AutomatorMessages.DEVICE_NOT_FOUND, environment.getAgentDeviceUuid()));
      throw new DeviceNotConnectedException("Couldn't find device " + StringUtils.defaultString(environment.getAgentDeviceUuid(), "") + ". Check if it's online.");
    }
  }

  private void setAppiumUrl(TestDeviceSettings testDeviceSettings) {
    String appiumServerUrl = mobileAutomationServerService.getMobileAutomationServer().getServerURL();
    log.info("Appium url - " + appiumServerUrl);
    testDeviceSettings.setAppiumUrl(appiumServerUrl);
  }

  private Platform getEnvPlatform() {
    if (environment.getEnvSettings().getPlatform() == null)
      return Platform.Generic;
    return environment.getEnvSettings().getPlatform();
  }

}
