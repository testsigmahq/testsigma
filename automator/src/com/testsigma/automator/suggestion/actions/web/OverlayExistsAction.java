package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;

public class OverlayExistsAction extends SuggestionAction {

  @Override
  public void execute() throws AutomatorException {
    getDriver().switchTo().alert();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
