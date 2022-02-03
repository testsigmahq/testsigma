package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllButtonWithTitleSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> buttons = getDriver().findElements(By.xpath("//button"));
    Assert.isTrue((buttons.size() != 0), String.valueOf(SuggestionSnippetResult.Failure));
    List<String> texts = new ArrayList<String>();
    for (WebElement button : buttons) {
      texts.add(button.getTagName());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", texts));
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
