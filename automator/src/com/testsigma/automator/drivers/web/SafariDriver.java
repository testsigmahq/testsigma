package com.testsigma.automator.drivers.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.entity.ExecutionLabType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.runners.EnvironmentRunner;
import com.testsigma.automator.utilities.PathUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class SafariDriver extends WebDriver {

  public SafariDriver() {
    super();
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    desiredCapabilities.setAcceptInsecureCerts(false);
    if (remoteServerURL != null) {
      remoteWebDriver = new RemoteWebDriver(remoteServerURL, new SafariOptions().merge(desiredCapabilities));
    } else {
      remoteWebDriver = new org.openqa.selenium.safari.SafariDriver(new SafariOptions().merge(desiredCapabilities));
    }
  }

  @Override
  protected void setTestsigmaLabCapabilities() throws AutomatorException {
    super.setTestsigmaLabCapabilities();
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
  }

  @Override
  public void setTimeouts() throws AutomatorException {
    String osVersion = getPlatform();
    int browserVersionValue = Integer.parseInt(getSafariVersion().split("\\.")[0]);

    if (((osVersion.equals("macOS 10.13") || osVersion.equals("macOS 10.14") || osVersion.equals("High Sierra")) ||
      (osVersion.equals("macOS 10.15") || osVersion.equals("Catalina"))) &&
      (browserVersionValue >= 12) && (executionLabType != ExecutionLabType.Hybrid)) {
      SessionId session = remoteWebDriver.getSessionId();
      Map<String, Object> timeouts = new HashMap<>();
      timeouts.put("implicit", settings.getElementTimeout() * 1000);
      timeouts.put("pageLoad", settings.getPageLoadTimeout() * 1000);
      try {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("/session/{sessionId}/timeouts").build()
          .expand(session.toString()).encode();
        String url = webDriverSettings.getWebDriverServerUrl() + uriComponents.toUriString();

        HttpClient httpClient = EnvironmentRunner.getWebAppHttpClient();
        String authHeader = HttpClient.BEARER + " " + getSettings().getJwtApiKey();
        HttpResponse<String> response = null;
        for (int i = 0; i < 3; i++) {
          try {
            response = httpClient.post(url, timeouts, new TypeReference<>() {
            }, authHeader);
            break;
          } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
          }
        }
        log.error(response.getStatusCode() + " - " + response.getResponseText());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new AutomatorException(e.getMessage(), e);
      }
    } else {
      super.setTimeouts();
    }
  }

  @Override
  protected void setupWebDriverManager() {
    log.debug("SafariDriver browser version from EnvironmentsSettings = " + settings.getBrowserVersion());

    WebDriverManager safariDriver = WebDriverManager.safaridriver();
    String safariDriverLocation = PathUtil.getInstance().getRootPath() + "/web-drivers";
    log.debug("safariDriverLocation = " + safariDriverLocation);
    safariDriver.cachePath(safariDriverLocation).setup();

    log.info("Downloaded SafariDriver version = " + safariDriver.getDownloadedDriverVersion());
    log.info("SafariDriver downloaded location = " + safariDriver.getDownloadedDriverPath());
  }
}
