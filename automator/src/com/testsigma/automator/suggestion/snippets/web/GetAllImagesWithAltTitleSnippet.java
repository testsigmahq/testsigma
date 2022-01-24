package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GetAllImagesWithAltTitleSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> images = getDriver().findElements(By.xpath("//img"));
    Assert.isTrue(images.size() != 0, String.valueOf(SuggestionSnippetResult.Failure));
    List<String> imageTexts = new ArrayList<String>();
    for (WebElement image : images) {
      imageTexts.add(image.getTagName());
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", imageTexts));
  }
}
