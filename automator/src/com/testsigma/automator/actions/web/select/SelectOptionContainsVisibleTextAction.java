package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

import java.util.List;

@Log4j2
public class SelectOptionContainsVisibleTextAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected an option whose text contains \"%s\". ";
  private static final String ELEMENT_NOT_FOUND = "Unable to find the select option which contains text <b>\"%s\"</b>." +
    "Please verify the select list with locator <b>\"%s:%s\"</b> has an option with text containing <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    String text = null;
    findElement();
    Select selectElement = new Select(getElement());
    List<WebElement> allOptions = selectElement.getOptions();
    for (WebElement option : allOptions) {
      String textData = option.getText();
      if (textData.contains(getTestData())) {
        text = textData;
        break;
      }
    }
    Assert.notNull(text, String.format(ELEMENT_NOT_FOUND, getTestDataMaskResult(), getFindByType(), getLocatorValue(), getTestDataMaskResult()));
    selectElement.selectByVisibleText(text);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}
