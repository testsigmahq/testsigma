package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.WebElement;

public class CheckElementIsNotInteractableSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    new GetElementSnippet().execute();
    if (((WebElement) getPreviousResult()).isEnabled()) {
      throw new Exception();
    }
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
