/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.FindByType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.NoSuchContextException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

@Log4j2
public abstract class MobileElementAction extends ElementAction {


  @Getter
  private WebElement element;
  @Getter
  private List<WebElement> elements;

  @Override
  protected void findElement() throws Exception {
    findElement(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
  }

  @Override
  protected void findElement(String elementActionVariableName) throws Exception {
    setElementSearchCriteria(elementActionVariableName);
    AppiumDriver driver = getDriver();
    if (this.getElementSearchCriteria().getFindByType().equals(FindByType.ACCESSIBILITY_ID)) {
//      elements = driver.findElementsByAccessibilityId(getElementSearchCriteria().getByValue());
      elements = driver.findElements(MobileBy.AccessibilityId(getElementSearchCriteria().getByValue()));
    } else {
      elements = ((WebDriver) driver).findElements(getElementSearchCriteria().getBy());
    }
    if (!elements.isEmpty()) {
      element = elements.get(0);
    } else {
      throw new NoSuchElementException(String.format("Element could not be found using the given criteria - \"%s:%s\"",
        getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
    }
  }

  @Override
  public AppiumDriver getDriver() {
    return (AppiumDriver) super.getDriver();
  }

  protected void verifyAlertPresence(String failureMessage) throws AutomatorException {
    try {
      getDriver().switchTo().alert();
    } catch (NoAlertPresentException e) {
      log.info("Alert is not present,", e);
      throw new AutomatorException(failureMessage);
    }
  }

  protected void handleStaleelementExecptionOnClickAction() throws Exception {
    int retriesTimeout = (getTimeout().toSecondsPart()) > 0 ? (getTimeout().toSecondsPart()) : 30;
    Long pollInterval = 500l;
    By by = getElementSearchCriteria().getBy();
    for (int i = 1; i <= (retriesTimeout * 2); i++) {
      try {
        log.debug("StaleElementReferenceException poll count:" + 1);
        getDriver().findElement(by).click();
        log.debug("StaleElementReferenceException avoided, No of polls:" + i);
        break;
      } catch (StaleElementReferenceException e) {
        if (i == (retriesTimeout * 2)) {
          throw e;
        } else {
          Thread.sleep(pollInterval);
        }

      }

    }
  }

  public void tapByElementCoOrdinates(WebElement webElement, AppiumDriver driver) throws Exception {
    Point loc = webElement.getLocation();
    Point center = getCenter(webElement);
    int x = center.getX();
    int y = center.getY();
    TapPointAction tapPointAction = new TapPointAction();
    tapPointAction.setTapPoint(new com.testsigma.automator.actions.mobile.TapPoint(x,
      y));
    tapPointAction.setDriver(driver);
    ActionResult result = tapPointAction.run();
    if (ActionResult.FAILED.equals(result)) {
      log.error(tapPointAction.getErrorMessage());
      throw new Exception("Failed to tap at (" + x + ", " + y + ") : " + tapPointAction.getErrorMessage());
    }
  }

  public Point getCenter(WebElement element) {
    Point upperLeft = element.getLocation();
    Dimension dimensions = element.getSize();
    return new Point(upperLeft.getX() + dimensions.getWidth() / 2, upperLeft.getY() + dimensions.getHeight() / 2);
  }

  public WebDriver context(String name) {
    AppiumDriver driver = getDriver();
    checkNotNull(name, "Must supply a context name");
    try {
      driver.execute(DriverCommand.SWITCH_TO_CONTEXT, ImmutableMap.of("name", name));
      return driver;
    } catch (WebDriverException e) {
      throw new NoSuchContextException(e.getMessage(), e);
    }
  }


  public Set<String> getContextHandles() {
    AppiumDriver driver = getDriver();
    Response response = driver.execute(DriverCommand.GET_CONTEXT_HANDLES, ImmutableMap.of());
    Object value = response.getValue();
    try {
      //noinspection unchecked
      List<String> returnedValues = (List<String>) value;
      return new LinkedHashSet<>(returnedValues);
    } catch (ClassCastException ex) {
      throw new WebDriverException(
              "Returned value cannot be converted to List<String>: " + value, ex);
    }
  }

  public String getCurrentContext() {
    AppiumDriver driver = getDriver();
    Response response = driver.execute(DriverCommand.GET_CURRENT_CONTEXT_HANDLE, ImmutableMap.of());
    Object value = response.getValue();
    try {
      //noinspection unchecked
      return (String) value;
    } catch (ClassCastException ex) {
      throw new WebDriverException(
              "Returned value cannot be converted to List<String>: " + value, ex);
    }
  }


}


