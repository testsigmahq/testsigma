package com.testsigma.automator.actions.mobile.mobileweb.tap;

import com.testsigma.automator.actions.ElementAction;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.HashMap;
import java.util.Map;

public class DoubleClickOnElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed double tap on element";

  @Override
  public void execute() throws Exception {
    findElement();
    if (getDriver() instanceof IOSDriver) {
      Map<String, Object> params = new HashMap<>();
      /*Dimension dim = getElement().getSize();
      params.put("x", dim.getWidth()/2);
      params.put("y", dim.getHeight()/2);
      params.put("element", ((MobileElement) getElement()).getId());*/
      params.put("element", getElement());
      ((IOSDriver) getDriver()).executeScript("mobile: doubleTap", params);
    } else {
      Actions actions = new Actions(getDriver());
      actions.moveToElement(getElement());
      actions.doubleClick();
      actions.build().perform();
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
