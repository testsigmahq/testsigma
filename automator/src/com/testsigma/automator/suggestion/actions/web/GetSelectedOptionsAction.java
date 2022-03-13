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

public class GetSelectedOptionsAction extends SuggestionAction {
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
