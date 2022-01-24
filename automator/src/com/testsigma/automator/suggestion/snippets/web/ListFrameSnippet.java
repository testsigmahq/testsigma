package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.LocatorType;
import com.testsigma.automator.actions.FindByType;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFrameSnippet extends SuggestionSnippet {


  public Object runSnippet() throws Exception {
    List<WebElement> iframes = getDriver().findElements(By.xpath("//iframe|//frame"));
    setPreviousResult(iframes);
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
    return iframes;
  }

  private List<WebElement> getElements(String elementActionVarName, LocatorType locatorType, String locatorValue) throws Exception {
    FindByType findByType = FindByType.getType(locatorType);
    ElementPropertiesEntity elementPropertiesEntity = new ElementPropertiesEntity();
    elementPropertiesEntity.setFindByType(findByType);
    elementPropertiesEntity.setLocatorValue(locatorValue);
    elementPropertiesEntity.setElementName("ui-iden");
    elementPropertiesEntity.setDynamicLocator(true);
    Map<String, ElementPropertiesEntity> elementPropertiesMap = getElementPropertiesEntityMap();
    if (elementPropertiesMap == null) {
      elementPropertiesMap = new HashMap<>();
      setElementPropertiesEntityMap(elementPropertiesMap);
    }
    getElementPropertiesEntityMap().put(elementActionVarName, elementPropertiesEntity);
    findElement(elementActionVarName);
    return getElements();
  }
}
