package com.testsigma.automator.actions.mobile.ios.scroll;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.HashMap;

public class ScrollToElementAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully scrolled to given element.";

  @Override
  protected void execute() throws Exception {
    findElement();
    RemoteWebElement targetElement = (RemoteWebElement) getElement();
    HashMap<String, String> scrollObject = new HashMap<>();
    scrollObject.put("element", targetElement.getId());
    scrollObject.put("toVisible", targetElement.getId());
    getDriver().executeScript("mobile: scroll", scrollObject);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
