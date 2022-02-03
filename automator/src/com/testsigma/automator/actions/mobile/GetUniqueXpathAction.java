package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.actions.FindByType;
import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;

public class GetUniqueXpathAction extends MobileDriverAction {
  @Getter
  @Setter
  Platform platform;

  @Getter
  @Setter
  MobileElement webElement;

  @Override
  protected void execute() throws Exception {
    AppiumDriver driver = getDriver();
    List<RemoteWebElement> webElements = new ArrayList<>();
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(FindByType.XPATH, webElement.getXpath());
    if(webElement.getWebViewName() != null) {
      driver.context(webElement.getWebViewName());
      webElements = driver.findElements(elementSearchCriteria.getBy());
      driver.context("NATIVE_APP");
    } else {
      webElements = driver.findElements(elementSearchCriteria.getBy());
    }

    if(webElements.size() > 1){

      String absoluteXpath = this.optimizeXpathUsingAttributes(webElement);
      elementSearchCriteria.setByValue(absoluteXpath);
      if(driver.findElements(elementSearchCriteria.getBy()).size() != 1){
        String relativeXpath = this.optimizeXpathRelatively(webElement);
        elementSearchCriteria.setByValue(relativeXpath);
        if (driver.findElements(elementSearchCriteria.getBy()).size() != 1){
          setActualValue(null);
        }else
          setActualValue(relativeXpath);
      } else {
        setActualValue(absoluteXpath);
      }
    } else {
      setActualValue(null);
    }
  }

  String optimizeXpathRelatively(MobileElement mobileElement){
    mobileElement.optimiseXpath = true;
    mobileElement.setContentDesc("");
    mobileElement.setResourceId("");
    mobileElement.setName("");
    mobileElement.setLabel("");
    mobileElement.setText("");
    mobileElement.populateXpath();
    return mobileElement.getXpath();
  }

  String optimizeXpathUsingAttributes(MobileElement mobileElement){
    mobileElement.optimiseXpath = true;
    mobileElement.populateXpath();
    return mobileElement.getXpath();
  }

}
