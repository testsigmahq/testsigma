package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

public class DoubleTapOnElementAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully executed double tap on element.";

  @Override
  protected void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    Map<String, Object> params = new HashMap<>();
    params.put("element", targetElement);
    getDriver().executeScript("mobile: doubleTap", params);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
