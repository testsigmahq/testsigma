package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListBoxesWithLabelAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> textLabels = getDriver().findElements(By.xpath("//select//preceding::label[1]"));
    Assert.isTrue(textLabels.size() != 0);
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    for (WebElement element : textLabels) {
      WebElement textBox = getDriver()
        .findElement(By.xpath("//label[text()='" + element.getText() + "']//following::select[1]"));
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("Element Text", element.getText());
      suggestions.put("Inner HTML", textBox.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
