package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.WebElement;

public class GetTagNameAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    new GetElementAction().execute();
    engineResult.getMetaData().setTagName(((WebElement) getPreviousResult()).getTagName());
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
