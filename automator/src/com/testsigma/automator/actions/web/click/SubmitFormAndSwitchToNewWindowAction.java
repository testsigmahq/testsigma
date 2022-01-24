package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubmitFormAndSwitchToNewWindowAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully performed click action and switched to new window.";
  private static final String FAILURE_NEW_WINDOW_NOT_OPENED = "Performed click action, but a new window is not opened.<br>" +
    "Please verify if the click action on element with locator <b>\"%s:%s\"</b> opens a new window.";
  private static final String FAILURE_NOT_SUBMITTED = "unable to submit the form. " +
    "Please verify that the given locator <b>\"%s:%s\"</b> is present in the form";

  @Override
  protected void execute() throws Exception {

    List<String> oldWindowsList = getWindowHandles();
    findElement();
    try {
      getElement().submit();
      List<String> newWindowsList = getWindowHandles();
      Assert.isTrue((oldWindowsList.size() < newWindowsList.size()),
        String.format(FAILURE_NEW_WINDOW_NOT_OPENED, getFindByType(), getLocatorValue()));
      getDriver().switchTo().window(newWindowsList.get(newWindowsList.size() - 1));
    } catch (Exception e) {
      throw new AutomatorException(String.format(FAILURE_NOT_SUBMITTED, getFindByType(), getLocatorValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  public List<String> getWindowHandles() {
    Set<String> oldWindows = getDriver().getWindowHandles();
    return new ArrayList<>(oldWindows);
  }
}
