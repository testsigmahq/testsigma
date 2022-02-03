package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

@Log4j2
public class SelectOptionByVisibleTextAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected an option by text. ";
  private static final String FAILURE_MESSAGE = "There is no element found with the given text <b>\"%s\"</b>" +
    " Please verify that the locator <b>\"%s:%s\"</b> contains an option with text <b>\"%s\"</b>.";
  private static final String ELEMENT_NOT_FOUND = "Unable to select option with given text <b>\"%s\"</b>." +
    " Please verify that the locator <b>\"%s:%s\"</b> contains an option with text <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    try {
      findElement();
      Select selectElement = new Select(getElement());
      selectElement.selectByVisibleText(getTestData());
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (NoSuchElementException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData(), getFindByType(), getLocatorValue(), getTestData()));
    }
  }
}
