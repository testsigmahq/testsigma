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

public class GetAllElementsWithTitleContains extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> titles = driver.findElements(By.xpath("//*[contains(@title,'" + testCaseStepEntity.getTestDataValue() + "')]"));
    Assert.isTrue((titles.size() != 0), String.valueOf(SuggestionActionResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement area : titles) {
      Map<String, String> data = new HashMap<>();
      data.put("elementtext", area.getText());
      list.add(data);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
