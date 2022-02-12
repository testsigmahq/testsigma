package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.By;

public class GetElementAction extends SuggestionAction {

  @Override
  public void execute() throws Exception {
    setPreviousResult(getDriver().findElement(By.xpath(testCaseStepEntity.getLocatorValue())));
    this.suggestionActionResult = SuggestionActionResult.Success;

  }
}
