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

public class GetSelectedOptionsSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> elementsWithLabels = driver.findElements(By.xpath("//*[preceding-sibling::label[text()='" + testCaseStepEntity.getTestDataValue() + "']]"));
    Assert.isTrue(elementsWithLabels.size() != 0);
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : elementsWithLabels) {
      Map<String, String> data = new HashMap<>();
      data.put("elementtext", element.getText());
      list.add(data);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
