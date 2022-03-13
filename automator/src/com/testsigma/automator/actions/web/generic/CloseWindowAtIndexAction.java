package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Set;

@Log4j2
public class CloseWindowAtIndexAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Closed window which is at index \"%s\".";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Selected index <b>\"%s\"</b> is not a number, please select index as number.";
  private static final String INDEX_NOT_AVAILABLE = "Given index <b>\"%s\"</b> is not valid. Total no. of windows/tabs in the browser is " +
    "<b>\"%s\"</b>,Index should be less than the total no. of windows/tabs. Index value starts from <b>0</b>,In current test max. possible " +
    "index value is <b>%s</b>.";
  private static final String WINDOW_NOT_AVAILABLE = "Closed window which is at index \"%s\". " +
    "There are no windows available after closing.";


  @Override
  public void execute() throws Exception {

    Set<String> windowHandles = getDriver().getWindowHandles();
    ArrayList<String> windows = new ArrayList<>(windowHandles);
    int index = NumberFormatter.getIntegerValue(getTestData(), String.format(ELEMENT_IS_NOT_A_NUMBER, getTestData()));
    Assert.isTrue((index < windows.size()), String.format(INDEX_NOT_AVAILABLE, getTestData(), windows.size(), windows.size() - 1));
    getDriver().switchTo().window(windows.get(index)).close();
    if (windows.size() > 1) {
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
      setSuccessMessage(String.format(WINDOW_NOT_AVAILABLE, getTestData()));
      return;
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
