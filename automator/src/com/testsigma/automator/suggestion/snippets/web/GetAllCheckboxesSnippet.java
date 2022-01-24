package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.LocatorType;
import com.testsigma.automator.actions.FindByType;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllCheckboxesSnippet extends SuggestionSnippet {
  @Override
  protected void execute() throws Exception {
    List<WebElement> elements = getElements("xpath", LocatorType.xpath, "//input[@type='checkbox']");
    List<Map<String, String>> list = new ArrayList<>();

    for (WebElement element : elements) {
      Map<String, String> suggestions = new HashMap<String, String>();
      suggestions.put("HTML Text", element.getAttribute("innerHTML"));
      list.add(suggestions);
    }
    engineResult.getMetaData().setSuggestions(new JSONObject().put("list", list));
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
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
