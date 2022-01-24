package com.testsigma.automator.suggestion.snippets.web;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;

public class CheckInvalidSelectorSnippet extends SuggestionSnippet {

  @Override
  protected void execute() throws Exception {
    try {
      getDriver().switchTo().alert();
      this.suggestionSnippetResult = SuggestionSnippetResult.Failure;
      throw new Exception();
    } catch (NoAlertPresentException exception) {
    }

    JavascriptExecutor jsExecutor = driver;
    Object frameSrc = jsExecutor.executeScript("return window.location.pathname");
    getDriver().switchTo().parentFrame();
    int size = getDriver().findElements(By.xpath("//iframe")).size();
    if (!frameSrc.toString().equals("/") || size > 0) {
      this.suggestionSnippetResult = SuggestionSnippetResult.Failure;
      if (!frameSrc.toString().equals("/")) {
        getDriver().switchTo().frame(getDriver().findElement(By.xpath("//iframe[@src='" + frameSrc + "']")));
      }
      throw new Exception();
    }

    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT);
    if (StringUtils.isBlank(elementPropertiesEntity.getLocatorValue())) {
      this.suggestionSnippetResult = SuggestionSnippetResult.Success;
      throw new Exception();
    }
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(elementPropertiesEntity.getFindByType(),
      elementPropertiesEntity.getLocatorValue());
    boolean isInvalid = false;
    try {
      getDriver().findElement(elementSearchCriteria.getBy());
      this.suggestionSnippetResult = SuggestionSnippetResult.Failure;
      isInvalid = true;
    } catch (Exception e) {
      this.suggestionSnippetResult = SuggestionSnippetResult.Success;
    }
    if (isInvalid) {
      throw new Exception();
    }
  }

}
