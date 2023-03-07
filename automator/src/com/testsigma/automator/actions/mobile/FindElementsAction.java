package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.ElementSearchCriteria;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FindElementsAction extends MobileElementAction {
  @Getter
  @Setter
  Platform platform;
  @Getter
  @Setter
  private ElementSearchCriteria elementSearchCriteria;

  @Override
  protected void execute() throws Exception {
    List<MobileElement> mobileElements = new ArrayList<MobileElement>();
    AppiumDriver driver = getDriver();
    List<WebElement> remoteWebElements = driver.findElements(getElementSearchCriteria().getBy());
    for (WebElement remoteWebElement : remoteWebElements) {
      mobileElements.add(new MobileElement((RemoteWebElement) remoteWebElement, platform));
    }
    Set<String> contextNames = getContextHandles();
    if (contextNames.size() > 1) {
      for (String name : contextNames) {
        if (name.equals("NATIVE_APP") || name.equals("WEBVIEW_chrome"))
          continue;
        context(name);
        remoteWebElements = driver.findElements(getElementSearchCriteria().getBy());
        for (WebElement remoteWebElement : remoteWebElements) {
          MobileWebElement mobileWebElement = new MobileWebElement((RemoteWebElement) remoteWebElement, platform);
          mobileWebElement.setWebViewName(name);
          mobileElements.add(mobileWebElement);
        }
        context("NATIVE_APP");
      }
    }
    setActualValue(mobileElements);
  }

}
