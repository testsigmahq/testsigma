package com.testsigma.constants;

public interface TSCapabilityType {
  String BROWSER_NAME = "browserName";
  String VERSION = "version";
  String DEVICE_ORIENTATION = "deviceOrientation";
  String PORTRAIT = "portrait";
  String UI_AUTOMATOR = "UiAutomator2";
  String XCUI_TEST = "XCUITest";
  String NO_RESET = "noReset";
  String BROWSER = "browser";
  String PLATFORM = "platform";
  String PLATFORM_NAME = "platformName";
  String BUNDLE_ID = "bundleId";
  String DEVICE_NAME = "deviceName";
  String APP = "app";
  String WDA_URL = "webDriverAgentUrl";
  String WDA_URL_VALUE = "http://localhost:8100";
  String AUTOMATION_NAME = "automationName";
  String PLATFORM_VERSION = "platformVersion";
  String KEY_RESOLUTION = "resolution";
  String DEFAULT_RESOLUTION = "1024x768";
  String SELENIUM_VERSION = "seleniumVersion";
  String EXTENDED_DEBUGGING = "extendedDebugging";
  String KEY_MAX_DURATION = "maxDuration";
  String KEY_MAX_IDLE_TIME = "idleTimeout";
  Integer MAX_DURATION = 10800;
  Integer MAX_IDLE_TIME = 300;
  String SKIP_DEVICE_INSTALLATION = "skipDeviceInstallation";
  String SKIP_SERVER_INSTALLATION = "skipServerInstallation";
  String UDID = "udid";
  String OS_VERSION = "os_version";
  String OS = "os";

  String TESTSIGMA_LAB_KEY_SCREEN_RESOLUTION = "screenResolution";
  String TESTSIGMA_LAB_NEW_COMMAND_TIMEOUT_CAP = "newCommandTimeout";
  String TESTSIGMA_LAB_COMMAND_TIMEOUT_CAP = "commandTimeout";
  int TESTSIGMA_LAB_NEW_COMMAND_TIMEOUT_VAL = 90 * 1000;
  int TESTSIGMA_LAB_COMMAND_TIMEOUT_VAL = 60 * 10 * 1000;

  String BROWSER_NAME_SAFARI = "safari";
}
