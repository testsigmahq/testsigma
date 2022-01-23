package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TakeFullPageScreenshotAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed Full Page Screenshot";

  @Override
  protected void execute() throws Exception {
    log.info("Full page screenshot is handled post test step execution.");
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
