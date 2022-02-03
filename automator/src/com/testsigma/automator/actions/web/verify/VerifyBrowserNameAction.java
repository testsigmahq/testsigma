package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Capabilities;
import org.springframework.util.Assert;

public class VerifyBrowserNameAction extends ElementAction {

  private static final String FAILURE_MESSAGE = "The browser name is not matching with expected value." +
    "<br>Expected: \"%s\" <br>Actual:\"%s\"";
  private static final String SUCCESS_MESSAGE = "Successfully verified browser name.";

  @Override
  public void execute() throws Exception {
    Capabilities sysCaps = getRemoteWebDriver().getCapabilities();
    setActualValue(sysCaps.getBrowserName());
    Assert.isTrue(getTestData().equalsIgnoreCase(getActualValue().toString()),
      String.format(FAILURE_MESSAGE, getTestDataMaskResult(), getActualValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
