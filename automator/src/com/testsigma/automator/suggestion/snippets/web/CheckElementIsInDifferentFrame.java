package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckElementIsInDifferentFrame extends SuggestionSnippet {

  @Override
  protected void execute() throws Exception {

    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT);
    if (StringUtils.isBlank(elementPropertiesEntity.getLocatorValue())) {
      throw new Exception();
    }
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(elementPropertiesEntity.getFindByType(),
      elementPropertiesEntity.getLocatorValue());
    WebElement elementInIframe = null;
    JavascriptExecutor jsExecutor = driver;
    String defaultPathName = (String) jsExecutor.executeScript("return window.location.pathname");
    List<WebElement> iframes = getDriver().findElements(By.xpath("//iframe|//frame"));
    Integer i = 1;
    for (WebElement iframe : iframes) {
      try {
        getDriver().switchTo().defaultContent();
        getDriver().switchTo().frame(iframe);
        elementInIframe = getDriver().findElement(elementSearchCriteria.getBy());
        Map<String, String> suggestions = new HashMap<String, String>();
        suggestions.put("Frame Name", "");//iframe.getAttribute("name")
        suggestions.put("Frame Index", new Integer(i).toString());
        engineResult.getMetaData().setSuggestions(new JSONObject().put("list", suggestions));
        this.suggestionSnippetResult = SuggestionSnippetResult.Success;
        break;
      } catch (Exception e) {
        continue;
      }
    }
    if (defaultPathName != jsExecutor.executeScript("return window.location.pathname")) {
      getDriver().switchTo().defaultContent();
      List<WebElement> elements = getDriver().findElementsByXPath("//iframe[@src=\"" + defaultPathName + "\"]");
      if (elements.size() > 0) {
        getDriver().switchTo().frame(elements.get(0));
      }
    }
    if (elementInIframe == null)
      throw exception;
  }
}
