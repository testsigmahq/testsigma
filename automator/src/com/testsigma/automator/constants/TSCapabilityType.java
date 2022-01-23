package com.testsigma.automator.constants;

import org.openqa.selenium.remote.CapabilityType;

public interface TSCapabilityType extends CapabilityType {
  String CHROME_DRIVER_EXECUTABLE_DIR = "chromedriverExecutableDir";
  String CHROME_DRIVER_EXECUTABLE = "chromedriverExecutable";
  String NAME = "name";
  String DEVICE = "device";
  String OS = "os";
  String BUNDLE_ID = "bundleId";
  String SELENIUM_VERSION = "seleniumVersion";
  String ACCEPT_SSL_CERTS = "acceptSslCerts";
  String UNHANDLED_PROMPT_BEHAVIOUR_KEY = "unhandledPromptBehavior";
  String UNHANDLED_PROMPT_BEHAVIOUR_VALUE = "ignore";
  String AVOID_PROXY = "avoidProxy";
  String BROWSER_DRIVER_PROPERTY_CHROME = "webdriver.chrome.driver";
  String BROWSER_DRIVER_PROPERTY_FIREFOX = "webdriver.gecko.driver";
  String BROWSER_DRIVER_PROPERTY_EDGE = "webdriver.edge.driver";
  String BROWSER_DRIVER_PROPERTY_IE = "webdriver.ie.driver";

  String FIREFOX_PROFILE = "firefoxprofile";
  String IE_ENSURE_CLEAN_SESSION = "ie.ensureCleanSession";
  String IE_IGNORE_ZOOM_SETTING = "ignoreZoomSetting";
  String IE_HANDLE_ALERTS = "handlesAlerts";
  String IE_IGNORE_PROTECTED_MODE_SETTINGS = "ignoreProtectedModeSettings";
  String IE_CSS_SELECTORS_ENABLED = "cssSelectorsEnabled";
  String IE_NATIVE_EVENTS = "nativeEvents";
  String IE_TAKE_SCREENSHOTS = "takesScreenshot";
  String TESTSIGMA_LAB_OPTIONS = "testsigma:options";
  String NEW_COMMAND_TIMEOUT = "newCommandTimeout";
  String APP = "app";
}
