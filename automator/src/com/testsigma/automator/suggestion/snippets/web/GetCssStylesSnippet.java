package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCssStylesSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    findElement();
    String styles = getElement().getAttribute("style");
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    Map<String, String> suggestions = new HashMap<String, String>();
    suggestions.put("Style", styles);
    list.add(suggestions);
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
