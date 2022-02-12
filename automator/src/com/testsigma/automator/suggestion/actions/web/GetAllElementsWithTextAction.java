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

public class GetAllElementsWithTextAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textAreas = driver.findElements(By.xpath("//*[text()='" + testCaseStepEntity.getTestDataValue() + "']"));
    Assert.isTrue(textAreas.size() != 0);
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement webElement : textAreas) {
      Map<String, String> data = new HashMap<>();
      data.put("elementtext", webElement.getText());
      list.add(data);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
