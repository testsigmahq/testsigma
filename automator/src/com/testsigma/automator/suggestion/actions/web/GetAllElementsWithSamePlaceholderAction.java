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

public class GetAllElementsWithSamePlaceholderAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> elementWithPlaceHolders = driver.findElements(By.xpath("//*[@placeholder='" + testCaseStepEntity.getTestDataValue() + "']"));
    Assert.isTrue((elementWithPlaceHolders.size() != 0), String.valueOf(SuggestionActionResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : elementWithPlaceHolders) {
      Map<String, String> data = new HashMap<>();
      data.put("elementtext", element.getAttribute("placeholder"));
      list.add(data);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
