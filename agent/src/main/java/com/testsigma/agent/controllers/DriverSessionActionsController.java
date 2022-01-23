/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.dto.MobileElementDTO;
import com.testsigma.agent.dto.ScreenDimensions;
import com.testsigma.agent.exception.MobileAutomationServerCommandExecutionException;
import com.testsigma.agent.mappers.MobileElementMapper;
import com.testsigma.agent.mobile.DriverSessionCommand;
import com.testsigma.agent.request.SendKeysRequest;
import com.testsigma.agent.request.TapPoint;
import com.testsigma.automator.entity.LocatorType;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.ElementSearchCriteria;
import com.testsigma.automator.actions.FindByType;
import com.testsigma.automator.actions.mobile.MobileElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.ScreenOrientation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(path = "/api/v1/session_actions/{session_id}")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverSessionActionsController {

  private final DriverSessionCommand driverSessionCommand;
  private final MobileElementMapper mobileElementMapper;

  /**
   * Tap on the current page at X,Y point
   *
   * @param sessionId
   * @param tapPoint
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/tap")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void tap(@PathVariable("session_id") String sessionId,
                  @RequestBody TapPoint tapPoint)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for tap received in session - " + sessionId + " at point - " + tapPoint);
    driverSessionCommand.tap(sessionId, tapPoint);
  }

  /**
   * swipe on the current page from point1 to point2
   *
   * @param sessionId
   * @param tapPoints
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/swipe")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void swipe(@PathVariable("session_id") String sessionId,
                    @RequestBody List<TapPoint> tapPoints)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for tap received in session - " + sessionId + " at point - " + tapPoints);
    driverSessionCommand.swipe(sessionId, tapPoints);
  }

  /**
   * Taps on the given mobile element in given remote web driver session.
   *
   * @param sessionId
   * @param mobileElement
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/tap_element")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void tapElement(@PathVariable("session_id") String sessionId,
                         @RequestBody MobileElement mobileElement)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for tap received in session - " + sessionId + " for element - " + mobileElement);
    driverSessionCommand.tapOnElement(sessionId, mobileElement);
  }

  /**
   * If this element is a text entry element, this will clear the value. Has no effect on other
   * elements. Text entry elements are INPUT and TEXTAREA elements.
   *
   * @param sessionId
   * @param mobileElement
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/clear_element")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void clearElement(@PathVariable("session_id") String sessionId,
                           @RequestBody MobileElement mobileElement)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for clear element received in session - " + sessionId + " for element - " + mobileElement);
    driverSessionCommand.clearElement(sessionId, mobileElement);
  }

  /**
   * Send keys to the given mobile element in given remote web driver session.
   *
   * @param sessionId
   * @param sendKeysRequest
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @PostMapping(value = "/send_keys")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendKeys(@PathVariable("session_id") String sessionId,
                       @RequestBody SendKeysRequest sendKeysRequest)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for send keys received in session - " + sessionId + " with keys - " + sendKeysRequest);
    driverSessionCommand.sendKeys(sessionId, sendKeysRequest.getMobileElement(), sendKeysRequest.getKeys());
  }

  /**
   * Takes a screenshot of the current page in the given session and returns the content
   *
   * @param sessionId
   * @return base64 encoded formatted image content as string value
   * @throws MobileAutomationServerCommandExecutionException
   */
  @GetMapping(value = "/screenshot", produces = MediaType.TEXT_PLAIN_VALUE)
  public String getScreenshot(@PathVariable("session_id") String sessionId) throws MobileAutomationServerCommandExecutionException {
    log.info("Request for screenshot in session - " + sessionId);
    return driverSessionCommand.pageScreenshot(sessionId);
  }

  /**
   * Gets the page source of the current page
   *
   * @param sessionId
   * @return MobileElementDTO - A tree structure of page element
   * @throws MobileAutomationServerCommandExecutionException
   */
  @GetMapping(value = "/page_source", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public MobileElementDTO getPageSource(@PathVariable("session_id") String sessionId, @RequestParam("platform") Platform platform)
    throws MobileAutomationServerCommandExecutionException {
    log.info("Request for page source in session - " + sessionId + " Platform - " + platform);
    MobileElement mobileElement = driverSessionCommand.pageSourceElements(sessionId, platform);
    MobileElementDTO mobileElementDTO = mobileElementMapper.map(mobileElement);
    return mobileElementDTO;
  }

  /**
   * navigate to the previous page from the current page in the current session
   *
   * @param sessionId
   * @return no application specific return value. Only corresponding http status codes.
   * @throws MobileAutomationServerCommandExecutionException
   */
  @GetMapping(value = "/navigate/back")
  @ResponseStatus(HttpStatus.OK)
  public void navigateBack(@PathVariable("session_id") String sessionId)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for navigate back in session - " + sessionId);
    driverSessionCommand.back(sessionId);
  }

  /**
   * Fetch the width and height of the current screen to which a session is in progress.
   *
   * @param sessionId
   * @return ScreenDimensions object which has screen height and width
   * @throws MobileAutomationServerCommandExecutionException
   */
  @GetMapping(value = "/screen_dimensions")
  @ResponseStatus(HttpStatus.OK)
  public ScreenDimensions screenDimensions(@PathVariable("session_id") String sessionId)
    throws MobileAutomationServerCommandExecutionException {

    log.info("Request for screen dimensions  in session - " + sessionId);
    return driverSessionCommand.getScreenDimensions(sessionId);
  }


  @GetMapping(value = "/find_elements")
  @ResponseStatus(HttpStatus.OK)
  public List<MobileElementDTO> findElements(@PathVariable("session_id") String sessionId,
                                             @RequestParam("platform") Platform platform,
                                             @RequestParam("locatorType") LocatorType locatorType,
                                             @RequestParam("byValue") String byValue)

    throws MobileAutomationServerCommandExecutionException {
    FindByType findByType = FindByType.getType(locatorType);
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(findByType, byValue);
    log.info("Request for searching Elements  in session - " + sessionId);
    List<MobileElement> mobileElements = driverSessionCommand.findElements(sessionId, platform, elementSearchCriteria);
    return mobileElementMapper.map(mobileElements);
  }

  @GetMapping(value = "navigate/home")
  @ResponseStatus(HttpStatus.OK)
  public void goToHome(@PathVariable("session_id") String sessionId) throws Exception {
    driverSessionCommand.goToHome(sessionId);
  }

  @GetMapping(value = "/change_orientation")
  @ResponseStatus(HttpStatus.OK)
  public void changeOrientation(@PathVariable("session_id") String sessionId) throws Exception {
    driverSessionCommand.changeOrientation(sessionId);
  }

  @GetMapping(value = "/get_orientation")
  @ResponseStatus(HttpStatus.OK)
  public ScreenOrientation getOrientation(@PathVariable("session_id") String sessionId) throws Exception {
    return driverSessionCommand.getOrientation(sessionId);
  }

  @PostMapping(value = "/search_and_send_keys")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void searchByIndexAndSendKeys(@PathVariable("session_id") String sessionId,
                                       @RequestParam("platform") Platform platform,
                                       @RequestParam("locatorType") LocatorType locatorType,
                                       @RequestParam("byValue") String byValue,
                                       @RequestParam("index") Integer index,
                                       @RequestParam("keys") String keys,
                                       @Nullable @RequestParam(value = "webViewName", required = false) String webViewName)
    throws Exception {
    FindByType findByType = FindByType.getType(locatorType);
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(findByType, byValue);
    log.info("Request for searching the Element By Index and Tapping on it, in session - " + sessionId +
      ",for locatorType - " + locatorType + ", byValue - " + byValue + ", index - " + index + ", keys -" + keys);
    driverSessionCommand.findElementByIndexAndSendKey(sessionId, platform, elementSearchCriteria, index, keys, webViewName);
  }

  @PostMapping(value = "/search_and_tap")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void searchByIndexAndTapElement(@PathVariable("session_id") String sessionId,
                                         @RequestParam("platform") Platform platform,
                                         @RequestParam("locatorType") LocatorType locatorType,
                                         @RequestParam("byValue") String byValue,
                                         @RequestParam("index") Integer index,
                                         @Nullable @RequestParam(value = "webViewName", required = false) String webViewName)
    throws Exception {
    FindByType findByType = FindByType.getType(locatorType);
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(findByType, byValue);
    log.info("Request for searching the Element By Index and Tapping on it, in session - " + sessionId +
      ",for locatorType - " + locatorType + ", byValue - " + byValue + ", index - " + index);
    driverSessionCommand.findElementByIndexAndTap(sessionId, platform, elementSearchCriteria, index, webViewName);
  }

  @PostMapping(value = "/search_and_clear")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void searchByIndexAndClearElement(@PathVariable("session_id") String sessionId,
                                           @RequestParam("platform") Platform platform,
                                           @RequestParam("locatorType") LocatorType locatorType,
                                           @RequestParam("byValue") String byValue,
                                           @RequestParam("index") Integer index,
                                           @Nullable @RequestParam(value = "webViewName", required = false) String webViewName)
    throws Exception {
    FindByType findByType = FindByType.getType(locatorType);
    ElementSearchCriteria elementSearchCriteria = new ElementSearchCriteria(findByType, byValue);
    log.info("Request for searching the Element By Index and Clearing it, in session - " + sessionId +
      ",for locatorType - " + locatorType + ", byValue - " + byValue + ", index - " + index);
    driverSessionCommand.findElementByIndexAndClear(sessionId, platform, elementSearchCriteria, index, webViewName);
  }

  @PostMapping(value = "/unique_xpath")
  @ResponseStatus(HttpStatus.OK)
  public String GetUniqueXpath(@PathVariable("session_id") String sessionId,
                               @RequestParam("platform") Platform platform,
                               @RequestBody MobileElement mobileElement)
    throws MobileAutomationServerCommandExecutionException {
    log.info("Request for Getting Unique Xpath of the Element, in session - " + sessionId);
    return driverSessionCommand.getUniqueXpath( sessionId, platform , mobileElement) ;
  }
}
