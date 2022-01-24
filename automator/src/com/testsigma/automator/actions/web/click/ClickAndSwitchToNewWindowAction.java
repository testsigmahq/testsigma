package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.actions.ActionsAction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

public class ClickAndSwitchToNewWindowAction extends ActionsAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed click action and switched to new window.";
  private static final String FAILURE_NEW_WINDOW_NOT_OPENED = "Performed click action, but a new window is not opened.<br>" +
    "Please verify if the click action on element with locator <b>\"%s:%s\"</b> opens a new window.";

  @Override
  protected void execute() throws Exception {
    List<String> oldWindowsList = getWindowsHandles();
    click(TESTS_TEP_DATA_MAP_KEY_ELEMENT, true);
    List<String> newWindowsList = getWindowsHandles();
    Assert.isTrue((oldWindowsList.size() < newWindowsList.size()),
      String.format(FAILURE_NEW_WINDOW_NOT_OPENED, getFindByType(), getLocatorValue()));
    getDriver().switchTo().window(newWindowsList.get(newWindowsList.size() - 1));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private List<String> getWindowsHandles() {
    Set<String> oldWindows = getDriver().getWindowHandles();
    return new ArrayList<>(oldWindows);
  }
}
