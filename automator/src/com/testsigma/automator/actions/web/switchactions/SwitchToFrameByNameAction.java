package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@Log4j2
public class SwitchToFrameByNameAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to frame with name <b>%s</b>.";

  @Override
  protected void execute() throws Exception {
    getDriver().switchTo().frame(getTestData());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    StringBuffer sb = new StringBuffer();
    try {
      List<WebElement> iframes = getDriver().findElements(By.tagName(ActionConstants.TAG_IFRAME));
      for (WebElement iframe : iframes) {
        String frameName = iframe.getAttribute(ActionConstants.ATTRIBUTE_NAME);
        sb.append(frameName + ",");
      }
    } catch (Exception ex) {
      log.info("Ignore error:", ex);
    }
    String errorMsg = String.format("%s<br> Available frames in page:<b>%s</b>", getErrorMessage(), sb);
    setErrorMessage(errorMsg);
  }
}

