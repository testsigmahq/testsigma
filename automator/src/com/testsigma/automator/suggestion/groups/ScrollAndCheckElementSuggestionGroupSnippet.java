package com.testsigma.automator.suggestion.groups;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import com.testsigma.automator.suggestion.snippets.web.ScrollSuggestionSnippet;

public class ScrollAndCheckElementSuggestionGroupSnippet extends SuggestionSnippet {
  @Override
  public void execute() throws Exception {
    new ScrollSuggestionSnippet().execute();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
