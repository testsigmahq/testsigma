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

public class GetAllElementsWithTitleAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> allElementsWithTitle = getDriver()
      .findElements(By.xpath("//a[@title='" + testCaseStepEntity.getTestDataValue() + "']"));
    Assert.isTrue((allElementsWithTitle.size() != 0), String.valueOf(SuggestionActionResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement webElement : allElementsWithTitle) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Inner HTML", webElement.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
