package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.ElementSearchCriteria;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FindElementsAction extends MobileDriverAction {
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
    List<RemoteWebElement> remoteWebElements = driver.findElements(getElementSearchCriteria().getBy());
    for (RemoteWebElement remoteWebElement : remoteWebElements) {
      mobileElements.add(new MobileElement(remoteWebElement, platform));
    }
    Set<String> contextNames = getDriver().getContextHandles();
    if (contextNames.size() > 1) {
      for (String name : contextNames) {
        if (name.equals("NATIVE_APP") || name.equals("WEBVIEW_chrome"))
          continue;
        driver.context(name);
        remoteWebElements = driver.findElements(getElementSearchCriteria().getBy());
        for (RemoteWebElement remoteWebElement : remoteWebElements) {
          MobileWebElement mobileWebElement = new MobileWebElement(remoteWebElement, platform);
          mobileWebElement.setWebViewName(name);
          mobileElements.add(mobileWebElement);
        }
        driver.context("NATIVE_APP");
      }
    }
    setActualValue(mobileElements);
  }

}
