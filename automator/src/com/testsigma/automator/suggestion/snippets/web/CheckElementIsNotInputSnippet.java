package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class CheckElementIsNotInputSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    new GetElementSnippet().execute();
    Assert.isTrue(!((WebElement) getPreviousResult()).getTagName().equalsIgnoreCase("input"));
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
