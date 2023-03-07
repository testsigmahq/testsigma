package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import io.appium.java_client.AppiumDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

@Log4j2
public class MobileInspectorTapOnElement extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tap action performed successfully";
  public final String ELEMENT_IS_DISABLED = "Unable to click on the element, Element is disabled";
  public final String ELEMENT_IS_NOT_DISPLAYED = "Unable to click on the element . Element is not displayed";

  @Override
  public void execute() throws Exception {
    AppiumDriver driver = getDriver();
    findElement();

    WebElement targetElement = getElement();
    try {
      if (!targetElement.isEnabled()) {
        throw new AutomatorException(ELEMENT_IS_DISABLED);
      } else if (!targetElement.isDisplayed()) {
        throw new AutomatorException(ELEMENT_IS_NOT_DISPLAYED);
      }
      if (getContextHandles().size() > 1) {
        tapByElementCoOrdinates(getElement(), driver);
      } else {
        targetElement.click();
      }
    } catch (StaleElementReferenceException staleException) {
      log.info("Encountered StaleElementReferenceException");
      handleStaleelementExecptionOnClickAction();
    }

    setSuccessMessage(SUCCESS_MESSAGE);
  }


  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof UnsupportedOperationException) {
      setErrorMessage(String.format("Unable click on search Keyboard. unsupported operation"));
      setErrorCode(ErrorCodes.PRESS_INVALID_OPERATION);
    }
  }
}
