package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;

public class GetWindowCountSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    engineResult.getMetaData().setTabCount(new Integer(getDriver().getWindowHandles().size()).toString());
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
