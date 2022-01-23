package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

@Log4j2
public class SelectOptionByValueAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected an option by value. ";
  private static final String FAILURE_MESSAGE = "There is no elements found with the given value <b>\"%s\"</b>. " +
    "Please verify that the locator <b>\"%s:%s\"</b> contains an option with value <b>\"%s\"</b>.";
  private static final String ELEMENT_NOT_FOUND = "Unable to select option with given value <b>\"%s\"</b>. " +
    "Please verify that the locator <b>\"%s:%s\"</b> contains an option with value <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    findElement();
    Select selectElement = new Select(getElement());
    try {
      selectElement.selectByValue(getTestData());
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (NoSuchElementException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestDataMaskResult(), getFindByType(), getLocatorValue(), getTestDataMaskResult()));
    }
  }
}
