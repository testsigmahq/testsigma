package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import org.openqa.selenium.By;
import org.springframework.util.Assert;

public class CheckModelExists extends SuggestionSnippet {

  @Override
  public void execute() throws Exception {
    Assert.isTrue(getDriver().findElements(By.xpath("//div[@class=\"modal\"][@role='dialog']")).size() > 0);
  }
}
