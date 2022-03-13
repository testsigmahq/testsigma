package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.WebElement;

public class CheckElementIsNotInteractableAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    new GetElementAction().execute();
    if (((WebElement) getPreviousResult()).isEnabled()) {
      throw new Exception();
    }
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
