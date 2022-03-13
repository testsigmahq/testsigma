package com.testsigma.automator.suggestion.actions.web;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;

public class CheckInvalidSelectorAction extends SuggestionAction {

  @Override
  protected void execute() throws Exception {
    try {
      getDriver().switchTo().alert();
      this.suggestionActionResult = SuggestionActionResult.Failure;
      throw new Exception();
    } catch (NoAlertPresentException exception) {
    }

    JavascriptExecutor jsExecutor = driver;
    Object frameSrc = jsExecutor.executeScript("return window.location.pathname");
    getDriver().switchTo().parentFrame();
    int size = getDriver().findElements(By.xpath("//iframe")).size();
    if (!frameSrc.toString().equals("/") || size > 0) {
      this.suggestionActionResult = SuggestionActionResult.Failure;
      if (!frameSrc.toString().equals("/")) {
        getDriver().switchTo().frame(getDriver().findElement(By.xpath("//iframe[@src='" + frameSrc + "']")));
      }
      throw new Exception();
    }

    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT);
    if (StringUtils.isBlank(elementPropertiesEntity.getLocatorValue())) {
      this.suggestionActionResult = SuggestionActionResult.Success;
      throw new Exception();
    }
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(elementPropertiesEntity.getFindByType(),
      elementPropertiesEntity.getLocatorValue());
    boolean isInvalid = false;
    try {
      getDriver().findElement(elementSearchCriteria.getBy());
      this.suggestionActionResult = SuggestionActionResult.Failure;
      isInvalid = true;
    } catch (Exception e) {
      this.suggestionActionResult = SuggestionActionResult.Success;
    }
    if (isInvalid) {
      throw new Exception();
    }
  }

}
