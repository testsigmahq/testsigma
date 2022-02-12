package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetElementValueAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    Map<String, String> suggestions = new HashMap<String, String>();
    suggestions.put("Element Value", getDriver().findElement(getBy()).getAttribute("value"));
    list.add(suggestions);
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
