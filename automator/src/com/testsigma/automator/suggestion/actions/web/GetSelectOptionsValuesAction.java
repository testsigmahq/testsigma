package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSelectOptionsValuesAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    Select select = new Select(getDriver().findElement(getBy()));
    List<WebElement> elements = select.getOptions();
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : elements) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Option Text", element.getText());
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
