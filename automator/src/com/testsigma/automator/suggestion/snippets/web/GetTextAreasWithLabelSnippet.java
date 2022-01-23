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

public class GetTextAreasWithLabelSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textLabels = getDriver().findElements(By.xpath("//textarea//preceding::label[1]"));
    Assert.isTrue(textLabels.size() != 0, String.valueOf(SuggestionSnippetResult.Failure));
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : textLabels) {
      WebElement textBox = getDriver()
        .findElement(By.xpath("//label[text()='" + element.getText() + "']//following::textarea[1]"));
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Element Text", element.getText());
      suggestions.put("Inner HTML", textBox.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
