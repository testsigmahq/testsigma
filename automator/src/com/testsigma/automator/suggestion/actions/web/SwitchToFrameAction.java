package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.WebElement;

public class SwitchToFrameAction extends SuggestionAction {

  public Object runAction() throws Exception {
    getDriver().switchTo().frame((WebElement) getPreviousResult());
    this.suggestionActionResult = SuggestionActionResult.Success;
    return null;
  }
}

