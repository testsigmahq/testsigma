package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.LocatorType;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.FindByType;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class DragAndDropAction extends ElementAction {
  @Override
  protected void execute() throws Exception {
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT);
    WebElement targetElementFrom = getElement();
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TO_ELEMENT);
    WebElement targetElementTo = getElement();
    Actions builder = new Actions(getDriver());
    Action dragAndDrop = builder.dragAndDrop(targetElementFrom, targetElementTo).build();
    dragAndDrop.perform();
    setSuccessMessage("Successfully executed drag and drop of element.");

  }

  private WebElement getElement(String elementActionVarName, LocatorType locatorType, String locatorValue) throws Exception {
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
    return getElement();
  }
}
