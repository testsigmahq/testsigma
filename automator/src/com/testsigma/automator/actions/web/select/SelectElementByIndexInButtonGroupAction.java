package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.List;

@Log4j2
public class SelectElementByIndexInButtonGroupAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected the option with index \"%s\" from button group. ";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Given index <b>\"%s\"</b> is not a number, please provide index numbers in test data.";
  private static final String ELEMENT_NOT_FOUND = "An option with the given index <b>\"%s\"</b> is not present in the specified button group.";
  private static final String INDEX_NOT_AVAILABLE = "Given index <b>\"%s\"</b> is not valid. Total no. of elements in the button group is " +
    "<b>\"%s\"</b>,Index should be less than the total no. of elements in the button group. Index value starts from <b>0</b>,In current test max. possible " +
    "index value is <b>%s</b>.";
  private static final String ELEMENT_NOT_CLICKABLE = "An element is found at given index <b>\"%s\"</b>, but unable to click on it." +
    "Please verify the element at given index is enabled and clickable.";

  @Override
  protected void execute() throws Exception {
    findElement();
    List<WebElement> targetElements = getElements();
    int indexValue = NumberFormatter.getIntegerValue(getTestData(), String.format(ELEMENT_IS_NOT_A_NUMBER, getTestDataMaskResult()));
    Assert.isTrue((indexValue < targetElements.size()), String.format(INDEX_NOT_AVAILABLE, indexValue, targetElements.size(), targetElements.size() - 1));
    for (int i = 0; i < targetElements.size(); i++) {
      if (i == indexValue) {
        String id = targetElements.get(i).getAttribute(ActionConstants.ATTRIBUTE_ID);
        try {
          getDriver().findElement(By.xpath("//label[@for='" + id + "']")).click();
          log.info(ActionResult.SUCCESS + " - " + getSuccessMessage());
          break;
        } catch (Exception e) {
          handleElementExceptions(e);
        }
      }
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }

  private void handleElementExceptions(Exception e) throws AutomatorException {
    if (e instanceof NotFoundException) {
      throw new AutomatorException(String.format(ELEMENT_NOT_FOUND, getTestDataMaskResult()));
    } else {
      throw new AutomatorException(String.format(ELEMENT_NOT_CLICKABLE, getTestDataMaskResult()));
    }
  }
}
