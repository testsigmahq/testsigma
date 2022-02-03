package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCurrentPageUrlSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    Map<String, String> suggestions = new HashMap<String, String>();
    suggestions.put("Current Page Url", getDriver().getCurrentUrl());
    list.add(suggestions);
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
