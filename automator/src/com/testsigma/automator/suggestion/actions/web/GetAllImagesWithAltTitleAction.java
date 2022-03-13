package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllImagesWithAltTitleAction extends SuggestionAction {
  @Override
  protected void execute() throws Exception {
    List<WebElement> images = getDriver().findElements(By.xpath("//img"));
    Assert.isTrue(images.size() != 0, String.valueOf(SuggestionActionResult.Failure));
    List<String> imageTexts = new ArrayList<String>();
    for (WebElement image : images) {
      imageTexts.add(image.getTagName());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", imageTexts));
  }
}
