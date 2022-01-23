package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetTextAreaWithTextSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textAreas = getDriver().findElements(By.xpath("//textarea"));
    Assert.isTrue(textAreas.size() != 0, String.valueOf(SuggestionSnippetResult.Failure));
    List<String> textAreaText = new ArrayList<>();
    for (WebElement area : textAreas) {
      textAreaText.add(area.getText());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", textAreaText));
  }
}
