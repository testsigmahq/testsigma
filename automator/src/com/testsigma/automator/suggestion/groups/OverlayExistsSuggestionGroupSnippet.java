package com.testsigma.automator.suggestion.groups;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import com.testsigma.automator.suggestion.snippets.web.CloseOverlaySnippet;
import com.testsigma.automator.suggestion.snippets.web.OverlayExistsSnippet;

public class OverlayExistsSuggestionGroupSnippet extends SuggestionSnippet {
  @Override
  public void execute() throws Exception {
    new OverlayExistsSnippet().run();
    new CloseOverlaySnippet().run();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
