package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListOfSelectWithOptionsAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> selectBoxesWithOption = getDriver().findElementsByXPath("//select/option[1]");
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    Integer i = 1;
    for (WebElement element : selectBoxesWithOption) {
      WebElement textBox = getDriver()
        .findElement(By.xpath("//label[text()='" + element.getText() + "']//following::select[" + i + "]"));
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Element Text", element.getText());
      if (textBox != null)
        suggestions.put("Inner HTML", textBox.getAttribute("innerHTML"));
      list.add(suggestions);
      i++;
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
