package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.By;

public class CloseOverlayAction extends SuggestionAction {

  @Override
  public void execute() throws Exception {
    getDriver().findElement(By.xpath("//button[@class='close']")).click();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
