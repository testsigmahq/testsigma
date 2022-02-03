package com.testsigma.automator.actions.web.scroll;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ActionsAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

@Log4j2
public class ScrollInsideElementToTopAction extends ActionsAction {

  private static final String SUCCESS_MESSAGE = "Successfully scrolled inside an element to top. ";
  private static final String FAILURE_MESSAGE = "unable to scroll inside an element with given locator <b>\"%s:%s\"</b>.";

  @Override
  public void execute() throws Exception {

    findElement();
    try {
      WebElement webElement = getElement();
      JavascriptExecutor js = (JavascriptExecutor) getDriver();
      js.executeScript("arguments[0].scrollTo(0,0);", webElement);
    } catch (Exception e) {
      log.error("unable to scroll inside an element", e);
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
