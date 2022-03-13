package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllImagesWithAltTextAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> images = getDriver().findElements(By.xpath("//img"));
    Assert.isTrue(images.size() != 0, String.valueOf(SuggestionActionResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement webElement : images) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Alt Text", webElement.getAttribute("alt"));
      suggestions.put("Inner HTML", webElement.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
