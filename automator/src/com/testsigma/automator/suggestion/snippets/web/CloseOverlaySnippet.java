package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.By;

public class CloseOverlaySnippet extends SuggestionSnippet {

  @Override
  public void execute() throws Exception {
    getDriver().findElement(By.xpath("//button[@class='close']")).click();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
