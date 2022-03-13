package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllTextboxWithTitleAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> alltextboxWithTitle = getDriver()
      .findElements(By.xpath("//input[@type='text'][@title='" + testCaseStepEntity.getTestDataValue() + "']"));
    Assert.isTrue(alltextboxWithTitle.size() != 0);
    List<String> listOfTextBoxes = new ArrayList<>();
    for (WebElement boxes : alltextboxWithTitle) {
      listOfTextBoxes.add(boxes.getText());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", listOfTextBoxes));
  }
}
