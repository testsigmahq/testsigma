package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;

public class SwitchToFrameByIndexAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to frame available at index <b>%s</b>.";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Selected index <b>\"%s\"</b> is not a number, please select index as number.";

  @Override
  protected void execute() throws Exception {
    int frameIndex = NumberFormatter.getIntegerValue(getTestData(), String.format(ELEMENT_IS_NOT_A_NUMBER, getTestDataMaskResult()));
    getDriver().switchTo().frame(frameIndex);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    setErrorMessage(getErrorMessage() + "<br>Frame index starts from zero. If there are two frames in page, possible index values are 0,1");
  }
}
