/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import com.testsigma.automator.entity.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.joox.JOOX;
import org.joox.Match;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;

@Log4j2
public class MobileWebViewElementsAction extends PageElementsAction {

  private static final String SUCCESS_MESSAGE = "Successfully fetched page source for given context %s";
  @Getter
  @Setter
  MobileWebElement pageElement;
  @Getter
  @Setter
  String context;
  @Getter
  @Setter
  MobileElement nativeReference;
  @Getter
  @Setter
  Platform platform;

  @Override
  public void execute() throws Exception {
    MobileWebElement mobileWebElement;
    getDriver().context(this.context);
    elementsDimensions();
    String webViewPageSource = getDriver().getPageSource();
    log.debug("Page source ::[" + context + "] fetched: " + webViewPageSource);
    webViewPageSource = webViewPageSource.replaceAll("&nbsp;", " ");

    org.jsoup.nodes.Document webViewDocument = Jsoup.parse(webViewPageSource);
    webViewDocument.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
    webViewDocument.html().getBytes(StandardCharsets.UTF_8);
    org.jsoup.nodes.Document webViewDocumentNew = Jsoup.parse(webViewDocument.html());
    W3CDom w3cDom = new W3CDom();
    org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(webViewDocumentNew);
    Match webViewMatch = JOOX.$(w3cDoc).xpath("//body/*[not(self::script)]");
    Document docnew = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element body = docnew.createElement("body");
    for (Node childNode : webViewMatch.get()) {
      Node imported_node = docnew.importNode(childNode, true);
      Element eElement = (Element) imported_node;
      body.insertBefore(eElement, null);
    }
    mobileWebElement = new MobileWebElement(body.cloneNode(true), 1, getPlatform(), this.context);
    mobileWebElement.setWebViewName(this.context);
    getDriver().context("NATIVE_APP");
    setActualValue(mobileWebElement);
    populateXpath(mobileWebElement);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, context));
  }

  private void elementsDimensions() {
    Integer width = nativeReference.getWidth();
    if (width == null)
      width = nativeReference.getX2() - nativeReference.getX1();
    Integer height = nativeReference.getHeight();
    if (height == null)
      height = nativeReference.getY2() - nativeReference.getY1();
    if (platform.equals(Platform.Android))
      getDriver().executeScript("return (function r(s, r, w, h){bodyRect = document.body.getBoundingClientRect(); hr = bodyRect.height/h; wr = bodyRect.width/w; o=document.body.getElementsByTagName('*'),i=window.devicePixelRatio;Array.from(o).forEach(t=>{const e=t.getBoundingClientRect(); t.setAttribute('data-testsigma-inspector-width',Math.round(Math.round(e.width*i))),t.setAttribute('data-testsigma-inspector-height',Math.round(Math.round(e.height*i))),t.setAttribute('data-testsigma-inspector-x',Math.round(s+e.left*i)),t.setAttribute('data-testsigma-inspector-y',Math.round(r+e.top*i))})}).apply(null, arguments)", this.nativeReference.getX1(), this.nativeReference.getY1(), width, height);
    else {
      try {
        Object statusBarHeight = getDriver().executeScript("return (function e(r){i=1,a=56,d=window.screen.height-window.innerHeight-r;return r+(d>=0&&d-a<0?d:a)*i}).apply(null, arguments)", 48);
        log.debug("statusBarHeight ::" + statusBarHeight);
        getDriver().executeScript("return (function r(r){o=document.body.getElementsByTagName('*'),i=1;Array.from(o).forEach(t=>{const e=t.getBoundingClientRect();t.setAttribute('data-testsigma-inspector-width',Math.round(e.width*i)),t.setAttribute('data-testsigma-inspector-height',Math.round(e.height*i)),t.setAttribute('data-testsigma-inspector-x',Math.round(e.left*i)),t.setAttribute('data-testsigma-inspector-y',Math.round(r+e.top*i))})}).apply(null, arguments)", statusBarHeight);
      } catch (Exception e) {
        log.error(e, e);
      }
    }
  }
}
