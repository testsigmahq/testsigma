package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckIfAlertIsPresentAction extends SuggestionAction {

  @Override
  protected void execute() throws Exception {
    getWebDriverWait().until(ExpectedConditions.alertIsPresent());
    this.suggestionActionResult = SuggestionActionResult.Success;
  }

}
