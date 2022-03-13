package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetButtonWithTextAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textAreas = getDriver().findElements(By.xpath("//button"));
    Assert.isTrue(textAreas.size() != 0);
    List<Map<String, String>> list = new ArrayList<>();
    for (WebElement textArea : textAreas) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Inner HTML", textArea.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
