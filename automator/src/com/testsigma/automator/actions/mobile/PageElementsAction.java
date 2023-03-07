/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.actions.constants.ErrorCodes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.joox.JOOX;
import org.joox.Match;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
public class PageElementsAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully fetched page source for current page";
  @Getter
  @Setter
  MobileElement pageElement;
  @Getter
  @Setter
  Platform platform;

  @Override
  public void execute() throws Exception {
    MobileElement mobileElement;
    context("NATIVE_APP");
    String pageSource = getDriver().getPageSource();

    log.debug("Page source fetched: " + pageSource);

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(new ByteArrayInputStream(pageSource.getBytes(StandardCharsets.UTF_8)));
    Match match = JOOX.$(document).xpath("//*");


    mobileElement = new MobileElement(match.get(0).cloneNode(true), 1, getPlatform());
    mobileElement.setParent(null);
    Set<String> contextNames = getContextHandles();
    mobileElement.setContextNames(contextNames);
    List<MobileWebElement> webViewElements = new ArrayList<>();
    List<MobileElement> webViewElementReferences = new ArrayList<>();
    if (contextNames.size() > 1) {
      this.findWebViewNativeElementReferences(mobileElement, webViewElementReferences);
      log.info("webViewElementReferences size:" + webViewElementReferences.size());
      log.info("No of Contexts:" + contextNames.size());
      log.info("Contexts:" + contextNames);
    }
    if (webViewElementReferences.size() > 0) {
      int webViewIndex = 0;
      for (String name : contextNames) {
        if (name.equals("NATIVE_APP") || name.equals("WEBVIEW_chrome"))
          continue;
        if (webViewIndex >= webViewElementReferences.size()) {
          break;
        }
        MobileWebViewElementsAction webViewSnippet = new MobileWebViewElementsAction();
        webViewSnippet.setDriver(getDriver());
        webViewSnippet.setContext(name);
        webViewSnippet.setPlatform(platform);
        webViewSnippet.setNativeReference(webViewElementReferences.get(webViewIndex));
        ActionResult result = webViewSnippet.run();
        if (result.equals(ActionResult.FAILED)) {
          log.error(webViewSnippet.getErrorMessage());
          continue;
          //throw new Exception("Failed to fetch page elements " + " : " + webViewSnippet.getErrorMessage());
        }
        MobileWebElement webViewParentElement = (MobileWebElement) webViewSnippet.getActualValue();
        MobileElement nativeRef = webViewSnippet.getNativeReference();
        //webViewParentElement.setParent(nativeRef.getParent());
        List<MobileElement> children = nativeRef.getParent().getChildElements();
        if (children == null)
          children = new ArrayList<>();
        children.add(children.size() - 1, webViewParentElement);
        nativeRef.getParent().setChildElements(children);
        webViewElements.add(webViewParentElement);
        webViewIndex++;
      }
    }
    mobileElement.setWebViewElements(webViewElements);
    context("NATIVE_APP");
    setActualValue(mobileElement);
    populateXpath(mobileElement);
    setSuccessMessage(SUCCESS_MESSAGE);

  }

  private void findWebViewNativeElementReferences(MobileElement mobileElement, List<MobileElement> elementRefs) {
    List<MobileElement> copedChildren = new ArrayList<>();
    if (mobileElement.getChildElements() != null)
      copedChildren.addAll(mobileElement.getChildElements());

    for (MobileElement element : copedChildren) {
      if (element.getType().contains("webkit.WebView")
        || element.getType().contains("XCUIElementTypeWebView")
        || element.getType().contains("XCUIElementTypeWindow")) {

        elementRefs.add(element);
      } else {
        findWebViewNativeElementReferences(element, elementRefs);
      }
    }
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    setErrorMessage(e.getMessage());
    setTimeoutException();
    setErrorCode(ErrorCodes.PAGE_ELEMENT_LOAD_TIMEOUT);
  }

  public Boolean sessionActive() {
    try {
      Response response = getDriver().getCommandExecutor().execute(new Command(getDriver().getSessionId(), "status"));
      return (response.getStatus() == 0) ? Boolean.FALSE : Boolean.TRUE;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Boolean.FALSE;
    }
  }

  private void setTimeoutException() {
    if (sessionActive() == Boolean.FALSE) {
      setErrorMessage("Session expired.");
    }
  }

  protected void populateXpath(MobileElement mobileElement) {
    mobileElement.populateXpath();
    if ((mobileElement.getChildElements() != null) && (mobileElement.getChildElements().size() > 0)) {
      for (MobileElement element : mobileElement.getChildElements()) {
        populateXpath(element);
      }
    }
  }
}
