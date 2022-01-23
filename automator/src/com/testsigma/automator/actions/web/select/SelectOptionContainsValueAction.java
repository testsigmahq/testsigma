package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

import java.util.List;

@Log4j2
public class SelectOptionContainsValueAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected an option whose value contains \"%s\". ";
  private static final String ELEMENT_NOT_FOUND = "Unable to find the select option which contains value <b>\"%s\"</b>.." +
    " Please verify the select list with locator <b>\"%s:%s\"</b> contains an option with value containing <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    String elementValue = null;
    findElement();
    Select selectElement = new Select(getElement());
    List<WebElement> allOptions = selectElement.getOptions();
    for (WebElement option : allOptions) {
      String value = option.getAttribute(ActionConstants.ATTRIBUTE_VALUE);
      if (value.contains(getTestData())) {
        elementValue = value;
        break;
      }
    }
    Assert.notNull(elementValue, String.format(ELEMENT_NOT_FOUND, getTestDataMaskResult(), getFindByType(), getLocatorValue(), getTestDataMaskResult()));
    selectElement.selectByValue(elementValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}
