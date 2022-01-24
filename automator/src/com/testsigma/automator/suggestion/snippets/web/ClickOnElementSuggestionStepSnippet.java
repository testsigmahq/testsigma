package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;

import java.util.HashMap;
import java.util.Map;

public class ClickOnElementSuggestionStepSnippet extends SuggestionSnippet {

  @Override
  public void execute() throws Exception {
    Map<String, Object> result = new HashMap<>();
    getDriver().findElement(getBy()).click();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
