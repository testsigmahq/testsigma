package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllWindowsTitleAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    ArrayList<String> windows = new ArrayList<String>(driver.getWindowHandles());
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (String window : windows) {
      driver.switchTo().window(window);
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Window Title", driver.getTitle());
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
