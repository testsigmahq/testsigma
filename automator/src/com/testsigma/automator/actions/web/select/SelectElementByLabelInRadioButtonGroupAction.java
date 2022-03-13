package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
public class SelectElementByLabelInRadioButtonGroupAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected the option with label text \"%s\" from radio button group. ";
  private static final String ELEMENT_NOT_FOUND = "An option with label <b>\"%s\"</b> is not present in the specified radio button group.";
  private static final String ELEMENT_NOT_CLICKABLE = "An element is found with label <b>\"%s\"</b>, but unable to click on it." +
    "Please verify the element with label <b>%s</b> is enabled and clickable.";

  @Override
  protected void execute() throws Exception {

    findElement();
    List<WebElement> targetElement = getElements();
    boolean elementSelect = false;
    for (WebElement button : targetElement) {
      try {
        String id = button.getAttribute(ActionConstants.ATTRIBUTE_ID);
        WebElement webElement = getDriver().findElement(By.xpath("//label[@for='" + id + "']"));
        if (webElement.getText().equals(getTestData())) {
          webElement.click();
          elementSelect = true;
          break;
        }
      } catch (Exception e) {
        handleElementExceptions(e);
      }
    }
    Assert.isTrue(elementSelect, String.format(ELEMENT_NOT_FOUND, getTestData()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }

  private void handleElementExceptions(Exception e) throws AutomatorException {
    if (e instanceof NoSuchElementException) {
      throw new AutomatorException(String.format(ELEMENT_NOT_FOUND, getTestData()));
    } else {
      throw new AutomatorException(String.format(ELEMENT_NOT_CLICKABLE, getTestData(), getTestData()));
    }
  }
}
