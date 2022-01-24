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

public class ListOfFramesSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    final List<WebElement> iframes = getDriver().findElements(By.tagName("iframe"));
    Assert.isTrue(iframes.size() != 0);
    Map<String, Integer> nameIndex = new HashMap<String, Integer>();
    int i = 1;
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    for (WebElement frame : iframes) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Frame Name", frame.getAttribute("name"));
      suggestions.put("Frame Index", new Integer(i).toString());
      list.add(suggestions);
      i++;
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
