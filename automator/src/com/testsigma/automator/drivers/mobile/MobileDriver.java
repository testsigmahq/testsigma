package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.TestsigmaDriver;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.entity.AppPathType;
import com.testsigma.automator.entity.WorkspaceType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.exceptions.TestsigmaException;
import com.testsigma.automator.mobile.ios.AppInstaller;
import com.testsigma.automator.mobile.ios.IosDeviceCommandExecutor;
import com.testsigma.automator.runners.EnvironmentRunner;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class MobileDriver extends TestsigmaDriver {

  public static final String APPIUM_INVALID_URL = "<br> For more information - <a href = \"https://support.testsigma.com/a/solutions/articles/32000023959-most-common-errors-appium-specific\" target=\"_blank\">https://support.testsigma.com/a/solutions/articles/32000023959-most-common-errors-appium-specific</a>";

  public MobileDriver() {
    super();
  }

  @Override
  protected void setCapabilities() throws AutomatorException, MalformedURLException {
    super.setCapabilities();
    List<WebDriverCapability> additionalCapabilitiesList = webDriverSettings.getWebDriverCapabilities();
    setCommonCapabilities();
    setPlatformSpecificCapabilities();
    setAdditionalCapabilities(additionalCapabilitiesList);
  }

  @Override
  protected void setCommonCapabilities() throws AutomatorException {
    super.setCommonCapabilities();
    capabilities.add(new WebDriverCapability(TSCapabilityType.NAME, executionName));
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
    setHybridRemoteServerUrl(settings.getAppiumUrl());
    if (WorkspaceType.isIOSNative(testDeviceEntity.getWorkspaceType()) &&
      (AppPathType.APP_DETAILS != settings.getAppPathType())) {
      log.info("Identified Application type is iOS Native and app path type is not using bundleID. Trying to resolve" +
        "bundle Id");
      List<WebDriverCapability> additionalCapabilitiesList = webDriverSettings.getWebDriverCapabilities();
      WebDriverCapability appCapability = additionalCapabilitiesList.stream().filter(cap -> cap.getCapabilityName()
        .equals(TSCapabilityType.APP)).findFirst().orElse(null);
      if ((appCapability != null) && StringUtils.isNotBlank(appCapability.getCapabilityValue().toString())) {
        AppInstaller appInstaller = new AppInstaller(EnvironmentRunner.getWebAppHttpClient());
        String bundleId = appInstaller.installApp(settings.getDeviceName(), settings.getDeviceUniqueId(),
          appCapability.getCapabilityValue().toString(), isDeviceAnEmulator(settings.getDeviceUniqueId()));
        log.info("Bundle Id From Installed Application - " + bundleId);
        settings.setBundleId(bundleId);
      }
    }
  }

  @Override
  protected RemoteWebDriver createDriver(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    try {
      Calendar startTime = Calendar.getInstance();
      createDriverInstance(desiredCapabilities);
      log.info("Stating with post mobile driver creation actions");
      Calendar endTime = Calendar.getInstance();
      log.info("Web Driver Session Created in - " + (endTime.getTimeInMillis() - startTime.getTimeInMillis()));
      setTimeouts();
      return remoteWebDriver;
    } catch (Exception e) {
      if (e.getCause() instanceof SSLException || e.getCause() instanceof ConnectException) {
        throw new AutomatorException(e.getCause() + String.format(APPIUM_INVALID_URL));
      } else {
        throw new AutomatorException(e.getMessage(), e);
      }
    }
  }

  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
  }

  public Boolean isDeviceAnEmulator(String udid) {
    try {
      IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
      if (SystemUtils.IS_OS_WINDOWS) {
        return false;
      }
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"describe", "--udid", udid, "--json"}, false);
      String deviceDescriptionJson = iosDeviceCommandExecutor.getProcessStreamResponse(p);
      JSONObject device = getProperties(deviceDescriptionJson);
      if (device.getString("target_type").equals("simulator")) {
        return true;
      }
    } catch(Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  private JSONObject getProperties(String deviceJson) throws TestsigmaException {
    try {
      log.info("Fetching device properties for device - " + deviceJson);
      JSONObject devicePropertiesJson = new JSONObject(deviceJson);
      log.info("Fetched device properties for device in json format - " + devicePropertiesJson);
      return devicePropertiesJson;
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }
}
