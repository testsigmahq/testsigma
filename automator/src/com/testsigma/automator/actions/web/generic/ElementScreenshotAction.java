package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ElementScreenshotAction extends ElementAction {

    @Override
    public void execute() throws Exception {
        findElement();
        log.info("Element screenshot is handled post test step execution.");
    }
}
