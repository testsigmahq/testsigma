package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.ElementSearchCriteria;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FindElementByIndexAndSendKeysAction extends MobileElementAction {
  @Getter
  @Setter
  Platform platform;
  @Getter
  @Setter
  Integer index;
  @Getter
  @Setter
  String webViewName;
  @Getter
  @Setter
  String keys;
  @Getter
  @Setter
  private ElementSearchCriteria elementSearchCriteria;

  @Override
  public void execute() throws Exception {
    AppiumDriver driver = getDriver();
    List<WebElement> webElements = new ArrayList<WebElement>();
    if (getWebViewName() != null && !getWebViewName().equals("null")) {
      context(getWebViewName());
      webElements = driver.findElements(getElementSearchCriteria().getBy());
      webElements.get(getIndex()).sendKeys(getKeys());
      context("NATIVE_APP");
    } else if (getContextHandles().size() > 1) {
      webElements = driver.findElements(getElementSearchCriteria().getBy());
      tapByElementCoOrdinates(webElements.get(getIndex()), driver);
      webElements.get(getIndex()).sendKeys(getKeys());
    } else {
      webElements = driver.findElements(getElementSearchCriteria().getBy());
      webElements.get(getIndex()).sendKeys(getKeys());
    }
  }
}
