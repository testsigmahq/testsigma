package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.springframework.util.Assert;

public class WaitUntilElementPropertyChangedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until attribute <b>%s</b> property is changed for the element.";
  private static final String FAILURE_MESSAGE = "<b>\"%s\"</b> attribute for the element located by <b>\"%s:%s\"</b> has not changed," +
    " Waited for <b>%s</b> seconds for element's attribute to change from previous value.<br>Old \"%s\" Attribute value:\"%s\"";

  @Override
  protected void execute() throws Exception {
    String oldAttributeValue = null;
    try {
      findElement();
      try {
        oldAttributeValue = getElement().getAttribute(getAttribute());
      } catch (WebDriverException we) {
        String message = we.getMessage();
        throw new AutomatorException(message.substring(message.indexOf("The attribute"), message.indexOf(")") + 1));
      }

      boolean isClassChanged = getWebDriverWait().until(CustomExpectedConditions.propertytobeChanged(getBy(), getAttribute(), oldAttributeValue));
      Assert.isTrue(isClassChanged, String.format(FAILURE_MESSAGE, getAttribute(), getFindByType(), getLocatorValue(),
        getTimeout(), getAttribute(), oldAttributeValue));
      setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute()));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getAttribute(), getFindByType(), getLocatorValue(),
        getTimeout(), getAttribute(), oldAttributeValue == null ? "" : oldAttributeValue));
    }
  }
}
