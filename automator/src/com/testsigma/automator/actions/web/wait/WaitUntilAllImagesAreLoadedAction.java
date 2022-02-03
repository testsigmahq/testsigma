package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

import java.util.List;

public class WaitUntilAllImagesAreLoadedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until all images in the page are displayed";
  private static final String FAILURE_MESSAGE = "Some/all images in the page are not displayed.<br>or<br>There are no images in the current page.";

  @Override
  public void execute() throws Exception {
    try {
      List<WebElement> elements = getWebDriverWait().until(ExpectedConditions.
        visibilityOfAllElementsLocatedBy(By.xpath("//img")));
      Assert.notNull(elements, FAILURE_MESSAGE);
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(FAILURE_MESSAGE, (Exception) e.getCause());
    }
  }
}
