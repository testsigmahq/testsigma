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

public class GetPasswordFieldsWithLabelSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> passwordsLabels = getDriver()
      .findElements(By.xpath("//input[@type='password']//preceding::label[1]"));
    Assert.isTrue(passwordsLabels.size() != 0, String.valueOf(SuggestionSnippetResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : passwordsLabels) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Text", element.getText());
      WebElement textBox = getDriver().findElement(
        By.xpath("//label[text()='" + element.getText() + "']//following::input[@type='password'][1]"));
      suggestions.put("Inner HTML", textBox.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
