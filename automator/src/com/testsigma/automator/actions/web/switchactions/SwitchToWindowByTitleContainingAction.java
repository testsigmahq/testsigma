package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;

import java.util.ArrayList;
import java.util.List;

public class SwitchToWindowByTitleContainingAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully switched to a window with title containing <b>%s</b>.";
  private static final String FAILURE_MESSAGE = "Failed to switch to window with title <b>%s</b>. There is no window with title <b>%s</b>." +
    "<br>Available Windows titles are:%s";

  @Override
  protected void execute() throws Exception {
    StringBuffer sb = new StringBuffer();

    String currentWindowHandle = getDriver().getWindowHandle();
    List<String> windowHandles = new ArrayList<>(getDriver().getWindowHandles());
    boolean isWindowFound = false;

    try {
      for (String window : windowHandles) {
        getDriver().switchTo().window(window);
        String title = getDriver().getTitle();
        sb.append(title).append("\n");
        //checks for the url when title tag is empty
        title = (title == null || title.isEmpty()) ? getDriver().getCurrentUrl() : title;
        if (title.toUpperCase().contains(getTestData().toUpperCase())) {
          isWindowFound = true;
          setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
          break;
        }
      }
    } finally {
      if (!isWindowFound) {
        getDriver().switchTo().window(currentWindowHandle);
        throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData(), getTestData(), sb));

      }
    }

  }
}




