package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRadioButtonWithTextSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> radioButtons = getDriver().findElements(By.xpath("//*[text()]/preceding::input[@type='radio']"));
    Assert.isTrue(radioButtons.size() != 0);
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : radioButtons) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Text", element.getText());
      suggestions.put("Inner HTML", element.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
