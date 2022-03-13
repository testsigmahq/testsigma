package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class GetAllCookiesAction extends SuggestionAction {


  @Override
  protected void execute() throws Exception {
    Set<Cookie> cookies = getDriver().manage().getCookies();
    Map<String, String> suggestions = new HashMap<String, String>();
    List cookieNames = cookies.stream().map(Cookie::getName).collect(Collectors.toList());
    suggestions.put("Cookies", String.join(" , ", cookieNames));
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", suggestions));
  }
}
