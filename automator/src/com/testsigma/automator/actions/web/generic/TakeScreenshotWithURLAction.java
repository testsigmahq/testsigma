package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TakeScreenshotWithURLAction extends ElementAction {
  @Override
  protected void execute() throws Exception {
    log.info("Page screenshot is handled post test step execution.");
  }
}
