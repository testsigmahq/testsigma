package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNewWindowOpenableLinksSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> links = getDriver().findElements(By.xpath("//a[@target='_blank']"));
    Assert.isTrue((links.size() != 0), "There are no links that can be opened on new window");
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : links) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Link Innner HTML", element.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
