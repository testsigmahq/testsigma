package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.openqa.selenium.JavascriptExecutor;

public class ScrollSuggestionAction extends SuggestionAction {

  @Override
  public void execute() throws Exception {
    scrollToElement();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }

  protected void scrollToElement() throws Exception {

    String scrollToElement = "try{ "
      + "arguments[0].scrollIntoView({"
      + " behavior: 'auto', block: 'center', inline: 'center'"
      + "}); return false;"
      + "}catch(e){"
      + "return true;"
      + "}";
    Object result = ((JavascriptExecutor) getDriver()).executeScript(scrollToElement,
      getDriver().findElement(getBy()));

    if (result instanceof Boolean && (Boolean) result) {
      String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, "
        + "window.innerHeight || 0);"
        + "var elementTop = arguments[0].getBoundingClientRect().top;"
        + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

      ((JavascriptExecutor) getDriver()).executeScript(scrollElementIntoMiddle,
        getDriver().findElement(getBy()));
    }
  }
}
