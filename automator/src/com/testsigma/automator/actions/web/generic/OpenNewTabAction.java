package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.JavascriptExecutor;

import java.util.ArrayList;
import java.util.Set;

public class OpenNewTabAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Opened new tab and switched to it. New test actions will be performed on the opened tab.";

  @Override
  protected void execute() throws Exception {
    ((JavascriptExecutor) getDriver()).executeScript("window.open()");
    Set<String> allWindows = getDriver().getWindowHandles();
    ArrayList<String> tabs = new ArrayList<>(allWindows);
    getDriver().switchTo().window(tabs.get(tabs.size() - 1));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
