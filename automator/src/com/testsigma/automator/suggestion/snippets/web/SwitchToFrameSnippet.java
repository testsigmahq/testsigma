package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.WebElement;

public class SwitchToFrameSnippet extends SuggestionSnippet {

  public Object runSnippet() throws Exception {
    getDriver().switchTo().frame((WebElement) getPreviousResult());
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
    return null;
  }
}

