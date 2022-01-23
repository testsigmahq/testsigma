package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllLinksWithTitleSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> links = getDriver().findElements(By.xpath("//a"));
    Assert.isTrue(links.size() != 0, String.valueOf((SuggestionSnippetResult.Failure)));
    List<String> texts = new ArrayList<String>();
    for (WebElement link : links) {
      texts.add(link.getAttribute("title"));
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", texts));
  }
}
