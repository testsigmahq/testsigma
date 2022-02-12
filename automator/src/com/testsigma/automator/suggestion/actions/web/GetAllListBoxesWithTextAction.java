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


public class GetAllListBoxesWithTextAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> listElementsWithText = getDriver().findElements(By.xpath("//*[text()]/preceding::select"));
    Assert.isTrue(listElementsWithText.size() != 0);
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : listElementsWithText) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Text", element.getText());
      suggestions.put("Inner HTML", element.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
