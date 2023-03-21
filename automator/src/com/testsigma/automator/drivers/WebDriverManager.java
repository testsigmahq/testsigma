package com.testsigma.automator.drivers;

import com.testsigma.automator.drivers.web.*;
import com.testsigma.automator.entity.Browsers;
import com.testsigma.automator.entity.OnAbortedAction;
import com.testsigma.automator.entity.TestDeviceSettings;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.runners.EnvironmentRunner;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public class WebDriverManager extends DriverManager {

  //TODO: All these fields can cause memory leak. Delete entries from these variable
  private final Map<String, String> windowHandles = new HashMap<>();
  private final Map<String, WebDriverSession> sessionMap = new HashMap<>();

  WebDriverManager() {
    super();
  }

  @Override
  protected RemoteWebDriver createDriverSession() throws AutomatorException, IOException {
    String browser = getTestDeviceSettings().getBrowser();

    switch (Browsers.getBrowser(browser)) {
      case GoogleChrome:
        setDriver(new ChromeDriver());
        break;
      case MozillaFirefox:
        setDriver(new FirefoxDriver());
        break;
      case Safari:
        setDriver(new SafariDriver());
        break;
      case MicrosoftEdge:
        setDriver(new EdgeDriver());
        break;
      default:
        throw new AutomatorException("Unknown browser type found. Unable to create corresponding driver session");
    }
    return getDriver().createSession();
  }

  @Override
  protected void afterSessionCreateActions()
    throws AutomatorException {
    super.afterSessionCreateActions();
    RemoteWebDriver driver = getDriver().getRemoteWebDriver();
    windowHandles.put(getExecutionUuid(), driver.getWindowHandle());
    setWebSession(driver);
  }

  @Override
  public void beforeEndSessionActions() throws AutomatorException {
    super.beforeEndSessionActions();
    windowHandles.remove(getExecutionUuid());
    sessionMap.remove(getExecutionUuid());
  }

  @Override
  public void performCleanUpAction(OnAbortedAction actionType) throws AutomatorException {
    switch (actionType) {
      case Restart_Session:
        log.info("On abort action - Ending session and marking session eligible for restart for execution id - "
          + getExecutionUuid());
        setRestartDriverSession(Boolean.TRUE);
        endSession();
        break;
      case Reuse_Session:
        log.info("On abort action - reusing session by performing actions: all windows except"
          + " main window will be closed and cookies will be deleted  for execution id - " + getExecutionUuid());
        closeAllWindowsExceptMainWindow();
        deleteCookies();
        break;
      default:
        log.error("Invalid action type is provided for on abort recovery actions");
    }
  }

  private void setWebSession(RemoteWebDriver driver) {
    TestDeviceSettings testDeviceSettings = EnvironmentRunner.getRunnerEnvironmentEntity().getEnvSettings();
    String executionUuid = EnvironmentRunner.getRunnerExecutionId();
    Capabilities cap = driver.getCapabilities();
    String browserVersion = cap.getCapability("browserVersion").toString();

    if (browserVersion.contains(".")) {
      browserVersion = browserVersion.substring(0, browserVersion.indexOf(".") + 2);
    }
    testDeviceSettings.setBrowserVersionFound(browserVersion);
    WebDriverSession webSession = new WebDriverSession();
    JSONObject sessionSettings = ObjectUtils.defaultIfNull(webSession.getSettings(), new JSONObject());
    for (String key : JSONObject.getNames(new JSONObject(testDeviceSettings))) {
      sessionSettings.put(key, new JSONObject(testDeviceSettings).get(key));
    }
    webSession.setSettings(sessionSettings);
    sessionMap.put(executionUuid, webSession);
  }

  private void closeAllWindowsExceptMainWindow() throws AutomatorException {
    try {
      String mainWindowHandle = getMainWindowHandle();
      if (mainWindowHandle != null) {
        WebDriver sessionDriver = getDriver().getRemoteWebDriver();
        Set<String> sessionHandles = sessionDriver.getWindowHandles();
        log.info("Windows currently open before cleanup : \"" + sessionDriver.getWindowHandles().size() + "\"");
        for (String sessionHandle : sessionHandles) {
          if (!sessionHandle.equals(mainWindowHandle)) {
            try {
              sessionDriver.switchTo().window(sessionHandle);
              sessionDriver.close();
              log.info("Window with window handle \"" + sessionHandle + "\" is closed");
            } catch (Exception e) {
              log.error("Error in closing window with window handle \"" + sessionHandle + "\" , details::" + e.getMessage());
            }
          }
        }
        log.info("Windows open after cleanup : \"" + sessionDriver.getWindowHandles().size() + "\"");
        // Retaining focus back to the main window
        try {
          sessionDriver.switchTo().window(mainWindowHandle);
          log.info("The focus is changed to the window with window handle \"" + mainWindowHandle + "\"");
        } catch (Exception e) {
          log.error("Error in switching to the window with window handle \"" + mainWindowHandle + "\" , details::" + e.getMessage());
        }

      } else {
        log.error("There is no main window handle associated with the given execution id \"" + getExecutionUuid() + "\"");
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private String getMainWindowHandle() {
    if (windowHandles.containsKey(getExecutionUuid())) {
      return windowHandles.get(getExecutionUuid());
    } else {
      log.error("There is no window handle stored for execution id \"" + getExecutionUuid() + "\"");
      return null;
    }
  }

  private void deleteCookies() {
    try {
      getDriver().getRemoteWebDriver().manage().deleteAllCookies();
      log.info("Deleted all cookies associated for webdriver instance associated with executionID:: " + getExecutionUuid());
    } catch (Exception e) {
      log.error("Error in deleting all cookies for webdriver instance associated with executionID:: "
        + getExecutionUuid() + ". Details:" + e.getMessage());
    }
  }

}
