package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrollTheElementIntoViewInsideFrame extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    WebElement elementInIframe = null;
    List<WebElement> iframes = getDriver().findElements(By.xpath("//iframe|//frame"));
    Integer i = 1;
    for (WebElement iframe : iframes) {
      try {
        getDriver().switchTo().defaultContent();
        getDriver().switchTo().frame(iframe);
        new ScrollSuggestionSnippet().execute();
        Map<String, String> suggestions = new HashMap<String, String>();
        suggestions.put("Frame Name", "");//iframe.getAttribute("name")
        suggestions.put("Frame Index", new Integer(i).toString());
        engineResult.getMetaData().setSuggestions(new JSONObject().put("list", suggestions));
        this.suggestionSnippetResult = SuggestionSnippetResult.Success;
        break;
      } catch (Exception e) {
        continue;
      }
    }
    if (elementInIframe == null)
      throw exception;
  }
}
