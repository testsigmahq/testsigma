package com.testsigma.automator.actions.mobile.ios.scroll;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.springframework.util.Assert;

import java.util.HashMap;

public class ScrollToGivenTextAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully scrolled to text <b>\"%s\"</b>";
  private static final String FAILURE_NOT_FOUND = "Given text <b>\"%s\"</b> is not found in current page.";

  @Override
  protected void execute() throws Exception {
    boolean bodytextContainsText = getDriver().getPageSource().toUpperCase().contains(getTestData().toUpperCase());
    Assert.isTrue(bodytextContainsText, String.format(FAILURE_NOT_FOUND, getTestData()));
    HashMap scrollObject = new HashMap<>();
    scrollObject.put("predicateString", "value == '" + getTestData() + "'");
    getDriver().executeScript("mobile: scroll", scrollObject);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}


