package com.testsigma.automator.suggestion.groups;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import com.testsigma.automator.suggestion.actions.web.ScrollSuggestionAction;

public class ScrollAndCheckElementSuggestionGroupAction extends SuggestionAction {
  @Override
  public void execute() throws Exception {
    new ScrollSuggestionAction().execute();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
