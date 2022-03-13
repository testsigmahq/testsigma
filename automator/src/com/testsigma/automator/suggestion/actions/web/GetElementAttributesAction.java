package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetElementAttributesAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    WebElement element = getDriver().findElement(getBy());
    JavascriptExecutor executor = getDriver();
    @SuppressWarnings("unchecked")
    List<String> attributes = (List<String>) executor.executeScript(
      "var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) "
        + "{ items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
      element);

    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (String attribute : attributes) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Attribute", attribute);
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));

  }
}
