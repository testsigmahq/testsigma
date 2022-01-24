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

public class GetAllElementsWithLabelContains extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> elementsWithLabels = driver.findElements(By.xpath("//*[preceding-sibling::label[contains(text(),'" + testCaseStepEntity.getTestDataValue() + "')]]"));
    Assert.isTrue((elementsWithLabels.size() != 0), String.valueOf(SuggestionSnippetResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement webelement : elementsWithLabels) {
      Map<String, String> str = new HashMap<>();
      str.put("tag", webelement.getText());
      list.add(str);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
