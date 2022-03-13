package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.support.ui.Select;

@Log4j2
public class SelectOptionByIndexAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected the option by index. ";
  private static final String FAILURE_MESSAGE = "There is no element found at given index <b>\"%s\"</b>" +
    " Please verify that the locator <b>\"%s:%s\"</b> contains an option at index <b>\"%s\"</b>.";
  private static final String ELEMENT_NOT_FOUND = "Unable to select option at given index <b>\"%s\"</b>." +
    " Please verify that the locator <b>\"%s:%s\"</b> contains an option at index <b>\"%s\"</b>.";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Given index <b>\"%s\"</b> is not a number, please provide index number in test data.";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    try {
      selectElement.selectByIndex(NumberFormatter.getIntegerValue(getTestData(), String.format(ELEMENT_IS_NOT_A_NUMBER, getTestData())));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (Exception e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData(), getFindByType(), getLocatorValue(), getTestData()));
    }
  }

}
