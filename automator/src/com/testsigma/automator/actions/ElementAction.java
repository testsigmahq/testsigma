/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions;


import com.testsigma.automator.entity.AttributePropertiesEntity;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.exceptions.ElementNotDisplayedException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.testsigma.automator.constants.NaturalTextActionConstants.*;

@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
public abstract class ElementAction extends DriverAction {
  protected static final String TRUE = "true";
  protected static final String FALSE = "false";
  protected static final String CHECKABLE = "checkable";
  protected static final String CHECKED = "checked";
  protected static final String CHECKBOX = "checkbox";
  protected static final String TYPE = "type";
  private static final String LOCATOR_VALUE_MISSING_ERROR_MSG = "element locatorValue is not available for Locator type:<b>%s</b>";

  private WebElement element;
  private List<WebElement> elements;
  private ElementSearchCriteria elementSearchCriteria;

  protected void setElementSearchCriteria(String elementActionVariableName) throws AutomatorException {
    log.debug("Setting element search criteria");
    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(elementActionVariableName);
    if (StringUtils.isBlank(elementPropertiesEntity.getLocatorValue())) {
      throw new AutomatorException(String.format(LOCATOR_VALUE_MISSING_ERROR_MSG, elementPropertiesEntity.getFindByType()));
    }
    elementSearchCriteria = new ElementSearchCriteria(elementPropertiesEntity.getFindByType(),
      elementPropertiesEntity.getLocatorValue());
  }

  protected void findElement(String elementActionVariableName) throws Exception {
    log.info("Finding an element for Action variable: " + elementActionVariableName);
    setElementSearchCriteria(elementActionVariableName);
    log.info(String.format("Finding element with criteria: %s, Explicit timeout as: %s", elementSearchCriteria, getTimeout()));
    CustomExpectedConditions.explictWait(getDriver(), elementSearchCriteria.getBy(), getTimeout().intValue());
    elements = getDriver().findElements(elementSearchCriteria.getBy());
    log.info("No of elements found: " + elements.size());
    if (!elements.isEmpty()) {
      setDisplayedElement();
    } else {
      throw new NoSuchElementException(String.format("Element could not be found using the given criteria - <b>\"%s:%s\"</b>", elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue()));
    }
  }

  protected By getBy() throws AutomatorException {
    return getBy(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
  }

  protected By getBy(String elementActionVariableName) throws AutomatorException {
    setElementSearchCriteria(elementActionVariableName);
    return elementSearchCriteria.getBy();
  }

  /*
  If multiple elements are matching given XPATH/locator, We can shortlist an element using different
  criteria for better expected element identification.
   */
  private void setDisplayedElement() {
    this.element = getElements().get(0);
    for (WebElement webElement : elements) {
      if (webElement.isEnabled()) {
        this.element = webElement;
        break;
      }
    }
  }

  protected void findElement() throws Exception {
    findElement(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
  }

  protected String getTestData() {
    return getTestData(TEST_STEP_DATA_MAP_KEY_TEST_DATA);
  }

  protected String getTestData(String testDataActionVarName) {
    TestDataPropertiesEntity testDataPropertiesEntity = getTestDataPropertiesEntity(testDataActionVarName);
    if(testDataPropertiesEntity == null) {
      return null;
    }
    return testDataPropertiesEntity.getTestDataValue();
  }

  protected String getTestDataType(String testDataNlpVarName) {
    TestDataPropertiesEntity testDataPropertiesEntity = getTestDataPropertiesEntity(testDataNlpVarName);
    return testDataPropertiesEntity.getTestDataType();
  }

  protected FindByType getFindByType() {
    return getFindByType(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
  }

  protected FindByType getFindByType(String elementActionVarName) {
    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(elementActionVarName);
    return elementPropertiesEntity.getFindByType();
  }

  protected String getLocatorValue() {
    return getLocatorValue(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
  }

  protected String getLocatorValue(String elementActionVarName) {
    ElementPropertiesEntity elementPropertiesEntity = getElementPropertiesEntity(elementActionVarName);
    return elementPropertiesEntity.getLocatorValue();
  }

  protected String getAttribute() {
    return getAttribute(TEST_STEP_DATA_MAP_KEY_ATTRIBUTE);
  }

  protected String getAttribute(String elementActionVarName) {
    AttributePropertiesEntity attributePropertiesEntity = getAttributePropertiesEntity(elementActionVarName);
    return attributePropertiesEntity.getAttributeName();
  }

  protected void constructElementWithDynamicXpath(String dynamicXpath) throws AutomatorException {
    constructElementWithDynamicXpath(dynamicXpath, TESTS_TEP_DATA_MAP_KEY_ELEMENT, TEST_STEP_DATA_MAP_KEY_TEST_DATA,
      TEST_STEP_DATA_MAP_KEY_ATTRIBUTE, false);
  }

  protected void constructElementWithDynamicXpath(String parameterizedXpath, String elementActionVarName, String testDataActionVarName,
                                                  String attributeActionVariableName, boolean isAttributeToBeReplacedWith) throws AutomatorException {
    ElementPropertiesEntity elementPropertiesEntity = new ElementPropertiesEntity();
    String xpath = parameterizedXpath;
    if (isAttributeToBeReplacedWith && attributeActionVariableName != null) {
      log.info("Constructing XPATH with Pre-defined syntax: " + parameterizedXpath + " and attribute:" + getAttribute(attributeActionVariableName));
      xpath = parameterizedXpath.replace(PARAMETERIZED_XPATH_PLACE_HOLDER, getAttribute(attributeActionVariableName));
    } else if (testDataActionVarName != null) {
      log.info("Constructing XPATH with Pre-defined syntax: " + parameterizedXpath + " and testdata:" + getTestData(testDataActionVarName));
      xpath = parameterizedXpath.replace(PARAMETERIZED_XPATH_PLACE_HOLDER, getTestData(testDataActionVarName));
    }

    log.info("Constructed XPATH:" + xpath);
    elementPropertiesEntity.setFindByType(FindByType.XPATH);
    elementPropertiesEntity.setLocatorValue(xpath);
    elementPropertiesEntity.setElementName("Dynamic element");
    elementPropertiesEntity.setDynamicLocator(true);
    Map<String, ElementPropertiesEntity> elementPropertiesMap = getElementPropertiesEntityMap();
    if (elementPropertiesMap == null) {
      elementPropertiesMap = new HashMap<>();
      setElementPropertiesEntityMap(elementPropertiesMap);
    }
    getElementPropertiesEntityMap().put(elementActionVarName, elementPropertiesEntity);
  }


  protected WebDriverWait getWebDriverWait() {
    return new WebDriverWait(getDriver(), getTimeout());
  }

  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NotFoundException) {
      handleNotFoundExceptionType(e);
    } else if (e instanceof InvalidElementStateException) {
      handleInvalidStateExceptionType(e);
    } else if (e instanceof StaleElementReferenceException) {
      handleStaleElementExceptionType(e);
    } else if (e instanceof UnhandledAlertException) {
      handleUnhandledAlertExceptionType(e);
    } else if (e instanceof JavascriptException) {
      handleJavaScriptException(e);
    } else if (e instanceof UnexpectedTagNameException) {
      handleUnExpectedTagNameException(e);
    } else if (e instanceof AutomatorException) {
      handleAutomatorException(e);
    } else if (e instanceof MoveTargetOutOfBoundsException) {
      handleMoveTargetOutOfBoundException(e);
    } else if (e instanceof NoSuchSessionException) {
      handleNoSuchSessionException(e);
    } else if (e instanceof SessionNotCreatedException || e instanceof UnreachableBrowserException) {
      handleNoSuchSessionException(e);
    }
  }

  private void handleNoSuchSessionException(Exception e) {
    setErrorCode(ErrorCodes.NO_SUCH_SESSION_EXCEPTION);
    setErrorMessage("The browser connection is lost. Either the browser is closed by the user or the connection is terminated.");
  }

  private void handleMoveTargetOutOfBoundException(Exception e) {
    String errorMessage = "The intended element is out of page view range. " +
      "Please scroll and make the element viewable and perform this action/step.";
    setErrorMessage(errorMessage);
    setErrorCode(ErrorCodes.MOVE_TARGET_OUT_OF_BOUND_EXCEPTION);
  }

  private void handleUnExpectedTagNameException(Exception e) {
    String errorMessage = e.getMessage().substring(0, e.getMessage().indexOf("\n"));
    errorMessage = (errorMessage != null) ? errorMessage : "Given locator/testdata is not pointing to an expected element type.";
    setErrorMessage(errorMessage);
    setErrorCode(ErrorCodes.UNEXPECTED_TAG_NAME_EXCEPTION);
  }

  private void handleJavaScriptException(Exception e) {
    setErrorMessage("Unable to execute Javascript - " + e.getMessage());
    setErrorCode(ErrorCodes.JAVA_SCRIPT_EXCEPTION);
  }

  private void handleAutomatorException(Exception e) {
    if (e instanceof ElementNotDisplayedException) {
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.ELEMENT_NOT_DISPLAYED);
    } else {
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.AUTOMATOR_EXCEPTION);
    }
  }

  private void handleUnhandledAlertExceptionType(Exception e) {
    //There are no sub types of this exception.
    String errorMessage = "There is an unhandled Alert in this page which is obstructing actions on the page/element. Alert Text:\"%s\"";
    String alertText = ((UnhandledAlertException) e).getAlertText();
    if (alertText == null) {
      alertText = e.getMessage();
      alertText = alertText.substring(alertText.indexOf("{") + 1, alertText.indexOf("}"));
      alertText = alertText.substring(alertText.indexOf(":") + 1);
    }
    setErrorMessage(String.format(errorMessage, alertText));
    setErrorCode(ErrorCodes.UNHANDLED_ALERT_EXCEPTION);
  }

  private void handleStaleElementExceptionType(Exception e) {
    if (elementSearchCriteria != null) {
      String errorMessage = "The element with locator <b>\"%s:%s\"</b> is removed and currently not present in the page due to dynamic updates on the element or the " +
        "page.-<a class=\"text-link\" href = \"https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements#problem7\" " +
        "target=\"_blank\">https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements#problem7</a>";
      setErrorMessage(String.format(errorMessage, elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue()));
    } else {
      String errorMessage = "The element state matching with given criteria is changed due to dynamic updates on the element or the " +
        "page. For more details, please visit below documentation.<br>-<a class=\"text-link\" href = \"https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements#problem7\" " +
        "target=\"_blank\">https://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements#problem7</a>";
      setErrorMessage(errorMessage);
    }
    setErrorCode(ErrorCodes.STALE_ELEMENT_EXCEPTION);

  }

  private void handleInvalidStateExceptionType(Exception e) {
    if (e instanceof ElementNotVisibleException) {
      String errorMessage;
      if (elementSearchCriteria != null) {
        errorMessage = String.format("Element may be present but not visible in current page. Please verify if the " +
          "element <b>\"%s:%s\"</b> is pointing to a displayed element.", elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue());
      } else {
        errorMessage = "Element may be present but not visible in current page. Please verify the given element criteria is pointing to a displayed/visible element.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_NOT_VISIBLE);
    } else if (e instanceof ElementClickInterceptedException) {
      String errorMessage = "Element may be present but unable to perform action. The element may be overlapped " +
        "or obscured by some other page element such as a Dialog box or an Alert.<br>" +
        "If the element is not in view, please try executing step <b>\"Scroll to the element ELEMENT into view\"</b></b>";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_CLICK_INTERCEPTED_EXCEPTION);
    } else if (e instanceof ElementNotSelectableException) {
      String errorMessage;
      if (elementSearchCriteria != null) {
        errorMessage = String.format("Element is present but it is not selectable. Please check if the select element " +
          "corresponding to locator <b>\"%s:%s\"</b> is enabled and interactable.", elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue());
      } else {
        errorMessage = "Element is present but it is not selectable. Please verify if the select element for given criteria is enabled and selectable.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.ELEMENT_NOT_SELECTABLE_EXCEPTION);
    } else {
      String errorMessage = "Cannot perform any action on the element. Though element may be present, it is in a non-interactive state.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.INVALID_ELEMENT_STATE_EXCEPTION);
    }
  }

  private void handleNotFoundExceptionType(Exception e) {
    if (e instanceof InvalidSelectorException) {
      String errorMessage;
      if (elementSearchCriteria != null) {
        errorMessage = String.format("Unable to find element with  locator<b> \"%s:%s\" </b>. Please verify XPATH syntax."
          , elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue());
      } else {
        errorMessage = "There is no element matching with given criteria/condition. Please verify the given element syntax.";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.INVALID_SELECTOR);
    } else if (e instanceof NoSuchElementException) {
      String errorMessage;
      if (elementSearchCriteria != null) {
        errorMessage = String.format("Element with locator<b>\"%s:%s\" </b> not found in current page.<br> Please visit below page for more details<br> <a class=\"text-link\"" +
          " href = \"http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements\" " +
          "target=\"_blank\">http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements</a>", elementSearchCriteria.getFindByType(), elementSearchCriteria.getByValue());
      } else {
        errorMessage = "There is no element matching with given criteria.<br> Please visit below page for more details<br> <a class=\"text-link\"" +
          " href = \"http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements\" " +
          "target=\"_blank\">http://support.testsigma.com/support/solutions/articles/32000024739-most-common-issues-caused-when-using-elements</a>";
      }
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_ELEMENT);
    } else if (e instanceof NoAlertPresentException) {
      String errorMessage = "There is no Alert present in this page.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_ALERT_PRESENT_EXCEPTION);
    } else if (e instanceof NoSuchContextException) {
      String errorMessage = "The context is not available. Please verify the context availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_CONTEXT_EXCEPTION);
    } else if (e instanceof NoSuchCookieException) {
      String errorMessage = "The Cookie is not available. Please verify the Cookie availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_COOKIE_EXCEPTION);
    } else if (e instanceof NoSuchFrameException) {
      String errorMessage = "The Frame is not available in current page. Please verify the frame availability";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_FRAME_EXCEPTION);
    } else if (e instanceof NoSuchWindowException) {
      String errorMessage = "The Window target is not available. Please verify the Window availability. Make sure window/tab is present while executing this test.";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NO_SUCH_WINDOW_EXCEPTION);
    } else {
      //Here we handle ParentException(NotFoundException), We should make sure all sub types are already handled before this.
      String errorMessage = "Cannot find element on this page";
      setErrorMessage(errorMessage);
      setErrorCode(ErrorCodes.NOT_FOUND_EXCEPTION);
    }
  }

  protected void updateErrorMessageForDynamicLocatorTypes(Exception e, String errorMessage) {
    if (e instanceof NotFoundException) {
      setErrorMessage(errorMessage);
    } else if (e instanceof InvalidElementStateException) {
      setErrorMessage(String.format("%s <br> Element is found but it is in invalid state. Please verify if the " +
        "desired element is obscured by another element or a dialog", errorMessage));
    }
  }
}
