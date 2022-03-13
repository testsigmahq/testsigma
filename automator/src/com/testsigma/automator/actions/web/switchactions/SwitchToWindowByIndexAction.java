package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SwitchToWindowByIndexAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to window available at index <b>%s</b>.";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Selected index <b>\"%s\"</b> is not a number, please select index as number.";
  private static final String FAILURE_INDEX_OUT_OF_BOUND = "Window/tab is not available at given index <b>%s</b>." +
    "<br>Total window/tab count in current session:<b>%s</b><br>Possible index values are from <b>0</b> to <b>%s</b> ";
  private List<String> windowsList = null;

  @Override
  protected void execute() throws Exception {
    int windowIndex = NumberFormatter.getIntegerValue(getTestData(), String.format(ELEMENT_IS_NOT_A_NUMBER, getTestData()));
    Set<String> windowHandles = getDriver().getWindowHandles();
    windowsList = new ArrayList<>(windowHandles);
    Assert.isTrue((windowIndex >= 0 && windowIndex < windowsList.size()),
      String.format(FAILURE_INDEX_OUT_OF_BOUND, windowIndex, windowsList.size(), windowsList.size() - 1));
    getDriver().switchTo().window(windowsList.get(windowIndex));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
