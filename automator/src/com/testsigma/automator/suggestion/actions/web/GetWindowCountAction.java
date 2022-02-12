package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;

public class GetWindowCountAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    engineResult.getMetaData().setTabCount(new Integer(getDriver().getWindowHandles().size()).toString());
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
