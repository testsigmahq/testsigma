package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckIfAlertIsPresentSnippet extends SuggestionSnippet {

  @Override
  protected void execute() throws Exception {
    getWebDriverWait().until(ExpectedConditions.alertIsPresent());
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }

}
