package com.testsigma.automator.actions.mobile.ios.tap;

import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class TapOnElementWhoseNameContainsAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully tapped on element using given name.";
  private static final String FAILURE_MESSAGE = "Elements are found with given locator <b>\"%s:%s\"</b>, " +
    "But none of the elements name contains given test data <b>\"%s\"</b>.<br>Below are the names for " +
    "the elements found with the given locator.<br><b>%s</b>";

  @Override
  protected void execute() throws Exception {
    findElement();
    List<String> names = new ArrayList<>();
    boolean isElementFound = false;
    for (WebElement element : getElements()) {
      String elementName = element.getAttribute(ActionConstants.ATTRIBUTE_NAME);
      names.add(elementName);
      if (elementName.contains(getTestData())) {
        element.click();
        isElementFound = true;
        break;
      }
    }
    Assert.isTrue(isElementFound, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTestData(), names));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
