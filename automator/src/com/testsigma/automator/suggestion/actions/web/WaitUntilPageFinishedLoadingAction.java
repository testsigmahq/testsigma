package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class WaitUntilPageFinishedLoadingAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    getWebDriverWait().until(CustomExpectedConditions.waitForPageLoadUsingJS());
    new GetElementAction().execute();
    Assert.isTrue(((WebElement) getPreviousResult()).isDisplayed());
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
