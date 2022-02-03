package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.JavascriptExecutor;

public class ScrollSuggestionSnippet extends SuggestionSnippet {

  @Override
  public void execute() throws Exception {
    scrollToElement();
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
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
