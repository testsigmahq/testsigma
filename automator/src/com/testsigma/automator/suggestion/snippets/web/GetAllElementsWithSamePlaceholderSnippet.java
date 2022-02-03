package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllElementsWithSamePlaceholderSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> elementWithPlaceHolders = driver.findElements(By.xpath("//*[@placeholder='" + testCaseStepEntity.getTestDataValue() + "']"));
    Assert.isTrue((elementWithPlaceHolders.size() != 0), String.valueOf(SuggestionSnippetResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : elementWithPlaceHolders) {
      Map<String, String> data = new HashMap<>();
      data.put("elementtext", element.getAttribute("placeholder"));
      list.add(data);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
