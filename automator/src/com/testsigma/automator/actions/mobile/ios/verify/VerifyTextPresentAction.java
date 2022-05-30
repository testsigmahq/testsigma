package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.util.Assert;

@Log4j2
public class VerifyTextPresentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that Given text is present in the page.";
  private static final String FAILURE_MESSAGE = "Text <b>%s</b> is not present in the page";

  @Override
  protected void execute() throws Exception {
    String pageSource = getDriver().getPageSource();
    Assert.isTrue(pageSource.contains(getTestData()), String.format(FAILURE_MESSAGE, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
