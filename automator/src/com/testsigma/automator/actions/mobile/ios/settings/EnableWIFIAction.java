package com.testsigma.automator.actions.mobile.ios.settings;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;

@Log4j2
public class EnableWIFIAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully enabled WIFI.";
  private static final String FAILURE_MESSAGE = "Unable to switch on WIFI in settings app.<br>Alternatively you can add steps for opening" +
    " settings app and navigate to WIFI. Below are the test steps.<br>" +
    "1.Open application with bundle id <test data> (Ex:com.apple.Preferences for settings app)<br>" +
    "2.Tap on <Element> (Ex XPATH Locator-{name:Wi-Fi})" +
    "3.Tap on <Element> (Ex XPATH Locator-{Xpath://XCUIElementTypeSwitch[@name='Wi-Fi']})";

  @Override
  protected void execute() throws Exception {
    //Switch to settings app
    ((IOSDriver) getDriver()).activateApp("com.apple.Preferences");
    WebElement wifiSettingsElement = getDriver().findElement(By.name("Wi-Fi"));
    wifiSettingsElement.click();
    String switchValue = null;
    for (int i = 0; i < 6; i++) {
      switchValue = getDriver().findElement(By.xpath("//XCUIElementTypeSwitch[@name='Wi-Fi']")).getAttribute("value");
      if (switchValue != null) {
        break;
      }
    }

    if (switchValue.equals("0")) {
      //Element is dynamically getting updated in few devices. So we refetch the element and execute a click action.
      getDriver().findElement(By.xpath("//XCUIElementTypeSwitch[@name='Wi-Fi']")).click();
    }
    //We switch back to parent app.
    String parentAppBundleID = (String) getDriver().getCapabilities().getCapability("bundleId");
    log.info("Parent App bundle ID:" + parentAppBundleID);
    boolean appRedirected = false;
    if (parentAppBundleID != null) {
      try {
        ((IOSDriver) getDriver()).activateApp(parentAppBundleID);
        appRedirected = true;
      } catch (Exception e) {
        //We ignore this exception, User can execute open app with bundleId step if switching failed.
        log.error("Unable to switch to parent App:", e);
      }
    }
    setSuccessMessage(SUCCESS_MESSAGE);
    if (!appRedirected) {
      setSuccessMessage(String.format("%s<br>If the app is not redirected to parent APP, use below step<br>" +
        "Open application with bundle id <test data>", SUCCESS_MESSAGE));
    }
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    setErrorMessage(FAILURE_MESSAGE + "<br>" + getErrorMessage());
  }
}
