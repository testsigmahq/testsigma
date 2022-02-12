package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class GetAllInputBoxesAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> inputs = getDriver().findElements(By.xpath("//input"));
    Assert.isTrue(inputs.size() != 0, String.valueOf(SuggestionActionResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : inputs) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Link Text", element.getText());
      suggestions.put("Inner HTML", element.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }

}
