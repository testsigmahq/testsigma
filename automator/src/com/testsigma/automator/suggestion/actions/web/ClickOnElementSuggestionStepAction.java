package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;

import java.util.HashMap;
import java.util.Map;

public class ClickOnElementSuggestionStepAction extends SuggestionAction {

  @Override
  public void execute() throws Exception {
    Map<String, Object> result = new HashMap<>();
    getDriver().findElement(getBy()).click();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
