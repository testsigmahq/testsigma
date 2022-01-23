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

public class GetAllElementsWithSameTextContainsSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textAreas = driver.findElements(By.xpath("//*[contains(text(),'" + testCaseStepEntity.getTestDataValue() + "')]"));
    Assert.isTrue((textAreas.size() != 0), String.valueOf(SuggestionSnippetResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement area : textAreas) {
      Map<String, String> data = new HashMap<>();
      if (!(area.getText().equals(""))) {
        data.put("elementtext", area.getText());
        list.add(data);
      }
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
