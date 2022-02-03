package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class WaitUntilPageFinishedLoadingSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    getWebDriverWait().until(CustomExpectedConditions.waitForPageLoadUsingJS());
    new GetElementSnippet().execute();
    Assert.isTrue(((WebElement) getPreviousResult()).isDisplayed());
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
