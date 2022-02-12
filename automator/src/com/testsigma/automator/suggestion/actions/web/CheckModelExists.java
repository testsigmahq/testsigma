package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.openqa.selenium.By;
import org.springframework.util.Assert;

public class CheckModelExists extends SuggestionAction {

  @Override
  public void execute() throws Exception {
    Assert.isTrue(getDriver().findElements(By.xpath("//div[@class=\"modal\"][@role='dialog']")).size() > 0);
  }
}
