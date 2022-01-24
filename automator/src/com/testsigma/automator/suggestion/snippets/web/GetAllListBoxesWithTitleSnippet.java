package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllListBoxesWithTitleSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> alllistboxWithTitle = getDriver().findElements(By.xpath("//select"));
    Assert.isTrue(alllistboxWithTitle.size() != 0);
    List<String> list = new ArrayList<String>();
    for (WebElement listBox : alllistboxWithTitle) {
      list.add(listBox.getText());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
  }
}
