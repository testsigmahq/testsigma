package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.By;

public class GetElementSnippet extends SuggestionSnippet {

  @Override
  public void execute() throws Exception {
    setPreviousResult(getDriver().findElement(By.xpath(testCaseStepEntity.getLocatorValue())));
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;

  }
}
