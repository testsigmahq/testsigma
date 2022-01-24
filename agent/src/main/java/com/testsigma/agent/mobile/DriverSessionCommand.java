/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.dto.ScreenDimensions;
import com.testsigma.agent.exception.MobileAutomationServerCommandExecutionException;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.actions.FindByType;
import com.testsigma.automator.actions.mobile.*;
import com.testsigma.automator.actions.mobile.android.switchactions.SwitchToContextWithNameAction;
import com.testsigma.automator.actions.mobile.android.switchactions.SwitchToNativeAppContextAction;
import io.appium.java_client.android.AndroidDriver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class DriverSessionCommand {
  private final SessionContainer sessionContainer;

  public void tap(String sessionId, com.testsigma.agent.request.TapPoint tapPoint) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      TapPointAction tapPointAction = new TapPointAction();
      tapPointAction.setTapPoint(new com.testsigma.automator.actions.mobile.TapPoint(tapPoint.getX(),
        tapPoint.getY()));
      tapPointAction.setDriver(remoteWebDriver);
      ActionResult result = tapPointAction.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(tapPointAction.getErrorMessage());
        throw new Exception("Failed to tap at " + tapPoint + " : " + tapPointAction.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void swipe(String sessionId, List<com.testsigma.agent.request.TapPoint> tapPoints) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      SwipeAction swipeAction = new SwipeAction();
      List<com.testsigma.automator.actions.mobile.TapPoint> targetTapPoints = new ArrayList<>();
      for (com.testsigma.agent.request.TapPoint tapPoint : tapPoints) {
        targetTapPoints.add(new com.testsigma.automator.actions.mobile.TapPoint(tapPoint.getX(), tapPoint.getY()));
      }
      swipeAction.setTapPoints(targetTapPoints.toArray(new com.testsigma.automator.actions.mobile.TapPoint[2]));
      swipeAction.setDriver(remoteWebDriver);
      ActionResult result = swipeAction.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(swipeAction.getErrorMessage());
        throw new Exception("Failed to swipe from " + tapPoints.get(0)
          + " to " + tapPoints.get(1) + " : " + swipeAction.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void tapOnElement(String sessionId, MobileElement mobileElement) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      if (mobileElement.getWebViewName() != null)
        this.switchToContextByName(remoteWebDriver, mobileElement);
      MobileInspectorTapOnElement mobileInspectorTapOnElement = new MobileInspectorTapOnElement();
      mobileInspectorTapOnElement.setElementPropertiesEntityMap(createElementPropertiesMap(FindByType.XPATH, mobileElement.getXpath()));
      mobileInspectorTapOnElement.setDriver(remoteWebDriver);
      ActionResult result = mobileInspectorTapOnElement.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(mobileInspectorTapOnElement.getErrorMessage());
        throw new Exception("Failed to tap on element " + " : " + mobileInspectorTapOnElement.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    } finally {
      if (mobileElement.getWebViewName() != null) {
        try {
          switchToNativeContext(sessionContainer.getSessionMap().get(sessionId), mobileElement);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
        }
      }
    }
  }

  private void switchToContextByName(RemoteWebDriver remoteWebDriver, MobileElement mobileElement) throws Exception {
    SwitchToContextWithNameAction switchToContext = new SwitchToContextWithNameAction();
    switchToContext.setTestDataPropertiesEntityMap(createTestDataPropertiesMap(mobileElement.getWebViewName()));
    switchToContext.setDriver(remoteWebDriver);
    ActionResult result = switchToContext.run();
    if (ActionResult.FAILED.equals(result)) {
      log.error(switchToContext.getErrorMessage());
      throw new Exception("Failed to Switch to context " + " : " + switchToContext.getErrorMessage());
    }
  }

  private void switchToNativeContext(RemoteWebDriver remoteWebDriver, MobileElement mobileElement) throws Exception {
    SwitchToNativeAppContextAction switchToContext = new SwitchToNativeAppContextAction();
    switchToContext.setDriver(remoteWebDriver);
    ActionResult result = switchToContext.run();
    if (ActionResult.FAILED.equals(result)) {
      log.error(switchToContext.getErrorMessage());
      throw new MobileAutomationServerCommandExecutionException("Failed to Switch to back to native context " + " : " + switchToContext.getErrorMessage());
    }
  }

  public void clearElement(String sessionId, MobileElement mobileElement) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      if (mobileElement.getWebViewName() != null)
        this.switchToContextByName(remoteWebDriver, mobileElement);
      ClearElementAction clearElementAction = new ClearElementAction();
      clearElementAction.setElementPropertiesEntityMap(createElementPropertiesMap(FindByType.XPATH, mobileElement.getXpath()));
      clearElementAction.setDriver(remoteWebDriver);
      ActionResult result = clearElementAction.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(clearElementAction.getErrorMessage());
        throw new Exception("Failed to clear element " + " : " + clearElementAction.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    } finally {
      if (mobileElement.getWebViewName() != null) {
        try {
          switchToNativeContext(sessionContainer.getSessionMap().get(sessionId), mobileElement);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
        }
      }
    }
  }

  private Map<String, ElementPropertiesEntity> createElementPropertiesMap(FindByType findByType, String locatorValue) {
    ElementPropertiesEntity elementPropertiesEntity = new ElementPropertiesEntity();
    Map<String, ElementPropertiesEntity> elementPropertiesEntityMap = new HashMap<>();
    elementPropertiesEntity.setFindByType(findByType);
    elementPropertiesEntity.setLocatorValue(locatorValue);
    elementPropertiesEntityMap.put(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT, elementPropertiesEntity);
    return elementPropertiesEntityMap;
  }

  private Map<String, TestDataPropertiesEntity> createTestDataPropertiesMap(String testDataValue) {
    Map<String, TestDataPropertiesEntity> testDataPropertiesEntityMap = new HashMap<>();
    TestDataPropertiesEntity testDataPropertiesEntity = new TestDataPropertiesEntity();
    testDataPropertiesEntity.setTestDataValue(testDataValue);
    testDataPropertiesEntityMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, testDataPropertiesEntity);
    return testDataPropertiesEntityMap;
  }

  public void sendKeys(String sessionId, MobileElement mobileElement, String keys) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      if (mobileElement.getWebViewName() != null)
        this.switchToContextByName(remoteWebDriver, mobileElement);
      SendKeysAction sendKeysAction = new SendKeysAction();
      sendKeysAction.setElementPropertiesEntityMap(createElementPropertiesMap(FindByType.XPATH, mobileElement.getXpath()));
      sendKeysAction.setTestDataPropertiesEntityMap(createTestDataPropertiesMap(keys));
      sendKeysAction.setDriver(remoteWebDriver);
      ActionResult result = sendKeysAction.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(sendKeysAction.getErrorMessage());
        throw new Exception("Failed to send keys to element " + " : " + sendKeysAction.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    } finally {
      if (mobileElement.getWebViewName() != null) {
        try {
          switchToNativeContext(sessionContainer.getSessionMap().get(sessionId), mobileElement);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
        }
      }
    }
  }


  public String pageScreenshot(String sessionId) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      ScreenshotAction screenshotAction = new ScreenshotAction();
      screenshotAction.setDriver(remoteWebDriver);
      ActionResult result = screenshotAction.run();
      if (ActionResult.FAILED.equals(result)) {
        log.error(screenshotAction.getErrorMessage());
        throw new Exception("Failed to take a screenshot " + " : " + screenshotAction.getErrorMessage());
      }
      return (String) screenshotAction.getActualValue();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public MobileElement pageSourceElements(String sessionId, Platform platform) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      PageElementsAction pageElementsAction = new PageElementsAction();
      pageElementsAction.setDriver(remoteWebDriver);
      pageElementsAction.setPlatform(platform);
      ActionResult result = pageElementsAction.run();
      if (result.equals(ActionResult.FAILED)) {
        log.error(pageElementsAction.getErrorMessage());
        throw new Exception("Failed to fetch page elements " + " : " + pageElementsAction.getErrorMessage());
      }
      return (MobileElement) pageElementsAction.getActualValue();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void back(String sessionId) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      MobileNavigateBackAction mobileNavigateBackAction = new MobileNavigateBackAction();
      mobileNavigateBackAction.setDriver(remoteWebDriver);
      ActionResult result = mobileNavigateBackAction.run();
      if (result.equals(ActionResult.FAILED)) {
        log.error(mobileNavigateBackAction.getErrorMessage());
        throw new Exception("Failed to navigate back to the previous page " + " : " + mobileNavigateBackAction.getErrorMessage());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void goToHome(String sessionId) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    if (remoteWebDriver.getClass().equals(AndroidDriver.class)) {
      com.testsigma.automator.actions.mobile.android.generic.GoToHomeScreenAction homeScreenAction = new com.testsigma.automator.actions.mobile.android.generic.GoToHomeScreenAction();
      homeScreenAction.setDriver(remoteWebDriver);
      homeScreenAction.execute();
    } else {
      com.testsigma.automator.actions.mobile.ios.generic.GoToHomeScreenAction homeScreenAction = new com.testsigma.automator.actions.mobile.ios.generic.GoToHomeScreenAction();
      homeScreenAction.setDriver(remoteWebDriver);
      homeScreenAction.execute();
    }
  }

  public ScreenDimensions getScreenDimensions(String sessionId) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      ScreenDimensionsAction screenDimensionsAction = new ScreenDimensionsAction();
      screenDimensionsAction.setDriver(remoteWebDriver);
      ActionResult result = screenDimensionsAction.run();
      if (result.equals(ActionResult.FAILED)) {
        log.error(screenDimensionsAction.getErrorMessage());
        throw new Exception("Failed to get device screen dimensions " + " : " + screenDimensionsAction.getErrorMessage());
      }
      ScreenDimensions screenDimensions = new ScreenDimensions();
      Dimension dimension = (Dimension) screenDimensionsAction.getActualValue();
      screenDimensions.setScreenHeight(dimension.getHeight());
      screenDimensions.setScreenWidth(dimension.getWidth());
      return screenDimensions;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public List<MobileElement> findElements(String sessionId, Platform platform, ElementSearchCriteria elementSearchCriteria) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      FindElementsAction findElementsAction = new FindElementsAction();
      findElementsAction.setDriver(remoteWebDriver);
      findElementsAction.setPlatform(platform);
      findElementsAction.setElementSearchCriteria(elementSearchCriteria);
      ActionResult result = findElementsAction.run();
      if (result.equals(ActionResult.FAILED)) {
        log.error(findElementsAction.getErrorMessage());
        throw new Exception("Failed to fetch searched elements " + " : " + findElementsAction.getErrorMessage());
      }
      return (List<MobileElement>) findElementsAction.getActualValue();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void findElementByIndexAndTap(String sessionId, Platform platform, ElementSearchCriteria elementSearchCriteria,
                                       Integer index, String webViewName) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    FindElementByIndexAndTapAction findElementByIndexAndTapAction = new FindElementByIndexAndTapAction();
    findElementByIndexAndTapAction.setDriver(remoteWebDriver);
    if (webViewName != null)
      findElementByIndexAndTapAction.setWebViewName(webViewName);
    findElementByIndexAndTapAction.setElementSearchCriteria(elementSearchCriteria);
    findElementByIndexAndTapAction.setIndex(index);
    findElementByIndexAndTapAction.setPlatform(platform);
    findElementByIndexAndTapAction.execute();
  }

  public void findElementByIndexAndSendKey(String sessionId, Platform platform, ElementSearchCriteria elementSearchCriteria,
                                           Integer index, String keys, String webViewName) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    FindElementByIndexAndSendKeysAction findElementByIndexAndSendKeysAction = new FindElementByIndexAndSendKeysAction();
    findElementByIndexAndSendKeysAction.setDriver(remoteWebDriver);
    if (webViewName != null)
      findElementByIndexAndSendKeysAction.setWebViewName(webViewName);
    findElementByIndexAndSendKeysAction.setElementSearchCriteria(elementSearchCriteria);
    findElementByIndexAndSendKeysAction.setIndex(index);
    findElementByIndexAndSendKeysAction.setPlatform(platform);
    findElementByIndexAndSendKeysAction.setKeys(keys);
    findElementByIndexAndSendKeysAction.execute();
  }


  public void findElementByIndexAndClear(String sessionId, Platform platform, ElementSearchCriteria elementSearchCriteria,
                                         Integer index, String webViewName) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    FindElementByIndexAndClearAction findElementByIndexAndClearAction = new FindElementByIndexAndClearAction();
    findElementByIndexAndClearAction.setDriver(remoteWebDriver);
    if (webViewName != null)
      findElementByIndexAndClearAction.setWebViewName(webViewName);
    findElementByIndexAndClearAction.setElementSearchCriteria(elementSearchCriteria);
    findElementByIndexAndClearAction.setIndex(index);
    findElementByIndexAndClearAction.setPlatform(platform);
    findElementByIndexAndClearAction.execute();
  }

  public void changeOrientation(String sessionId) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    if (remoteWebDriver.getClass().equals(AndroidDriver.class)) {
      com.testsigma.automator.actions.mobile.android.generic.ChangeScreenOrientationAction changeScreenOrientationAction = new com.testsigma.automator.actions.mobile.android.generic.ChangeScreenOrientationAction();
      changeScreenOrientationAction.setDriver(remoteWebDriver);
      changeScreenOrientationAction.execute();
    } else {
      com.testsigma.automator.actions.mobile.ios.generic.ChangeScreenOrientationAction changeScreenOrientationAction = new com.testsigma.automator.actions.mobile.ios.generic.ChangeScreenOrientationAction();
      changeScreenOrientationAction.setDriver(remoteWebDriver);
      changeScreenOrientationAction.execute();
    }
  }

  public ScreenOrientation getOrientation(String sessionId) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    ScreenOrientation orientation;
    if (remoteWebDriver.getClass().equals(AndroidDriver.class)) {
      com.testsigma.automator.actions.mobile.android.generic.GetScreenOrientationAction getScreenOrientationAction = new com.testsigma.automator.actions.mobile.android.generic.GetScreenOrientationAction();
      getScreenOrientationAction.setDriver(remoteWebDriver);
      getScreenOrientationAction.execute();
      orientation = (ScreenOrientation) getScreenOrientationAction.getActualValue();
    } else {
      com.testsigma.automator.actions.mobile.ios.generic.GetScreenOrientationAction getScreenOrientationAction = new com.testsigma.automator.actions.mobile.ios.generic.GetScreenOrientationAction();
      getScreenOrientationAction.setDriver(remoteWebDriver);
      getScreenOrientationAction.execute();
      orientation = (ScreenOrientation) getScreenOrientationAction.getActualValue();
    }
    return orientation;
  }

  public String getUniqueXpath(String sessionId, Platform platform, MobileElement mobileElement) throws MobileAutomationServerCommandExecutionException {
    try {
      RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
      GetUniqueXpathAction getUniqueXpathSnippet = new GetUniqueXpathAction();
      getUniqueXpathSnippet.setDriver(remoteWebDriver);
      getUniqueXpathSnippet.setPlatform(platform);
      getUniqueXpathSnippet.setWebElement(mobileElement);
      ActionResult result = getUniqueXpathSnippet.run();
      if (result.equals(ActionResult.FAILED)) {
        log.error(getUniqueXpathSnippet.getErrorMessage());
        throw new Exception("Failed to get Unique Xpath" + " : " + getUniqueXpathSnippet.getErrorMessage());
      }
      return  (String) getUniqueXpathSnippet.getActualValue();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }
}
