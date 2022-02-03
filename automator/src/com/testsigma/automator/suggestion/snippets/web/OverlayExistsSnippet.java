package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;

public class OverlayExistsSnippet extends SuggestionSnippet {

  @Override
  public void execute() throws AutomatorException {
    getDriver().switchTo().alert();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
