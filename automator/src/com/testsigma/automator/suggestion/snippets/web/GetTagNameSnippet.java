package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.WebElement;

public class GetTagNameSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    new GetElementSnippet().execute();
    engineResult.getMetaData().setTagName(((WebElement) getPreviousResult()).getTagName());
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
