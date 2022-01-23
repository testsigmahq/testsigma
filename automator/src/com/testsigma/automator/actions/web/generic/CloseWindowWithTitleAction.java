package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Set;

@Log4j2
public class CloseWindowWithTitleAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Closed window with title \"%s\".";
  private static final String FAILURE_WINDOW_NOT_AVAILABLE = "There is no window/tab with title <b>\"%s\"</b>." +
    "<br>Available Windows titles are:%s";
  private static final String WINDOW_NOT_AVAILABLE = "Closed window with title \"%s\". " +
    "There are no windows available after closing.";

  @Override
  public void execute() throws Exception {
    Set<String> windowHandles = getDriver().getWindowHandles();
    StringBuffer sb = new StringBuffer();
    boolean windowClosed = false;
    for (String windowHandle : windowHandles) {
      getDriver().switchTo().window(windowHandle);
      String title = getDriver().getTitle();
      sb.append(title).append("\n");
      if (title.equals(getTestData().trim())) {
        log.info("Closing window/tab,Window title:" + getTestDataMaskResult());
        getDriver().close();
        windowClosed = true;
        break;
      }
    }
    Assert.isTrue(windowClosed, String.format(FAILURE_WINDOW_NOT_AVAILABLE, getTestDataMaskResult(), sb));
    if (windowHandles.size() > 1) {
      Set<String> switchWindows = getDriver().getWindowHandles();
      if (!switchWindows.isEmpty()) {
        ArrayList<String> newWindows = new ArrayList<>(switchWindows);
        try {
          getDriver().switchTo().window(newWindows.get(newWindows.size() - 1));
        } catch (Exception e) {
          log.error("Ignoring error while switching ", e);
        }
      }
    } else {
      setSuccessMessage(String.format(WINDOW_NOT_AVAILABLE, getTestDataMaskResult()));
      return;
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }

}
