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
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

@Data
@Log4j2
public class MobileWebElement extends MobileElement {

  public MobileWebElement(RemoteWebElement remoteWebElement, Platform platform) {
    super(remoteWebElement, platform);
    this.uuid = UUID.randomUUID().toString();
    this.setName(remoteWebElement.getTagName());
    this.setName(remoteWebElement.getTagName());
    this.setType(remoteWebElement.getAttribute("class"));
    this.setResourceId(remoteWebElement.getAttribute("resource-id"));
    this.setContentDesc(remoteWebElement.getAttribute("content-desc"));
    this.setAccessibilityId(this.getContentDesc());
    this.setPassword(Boolean.valueOf(remoteWebElement.getAttribute("password")));
    this.setClickable(Boolean.valueOf(remoteWebElement.getAttribute("clickable")));
    this.setChecked(Boolean.valueOf(remoteWebElement.getAttribute("checked")));
    this.setLongClickable(Boolean.valueOf(remoteWebElement.getAttribute("longClickable")));
    this.setScrollable(Boolean.valueOf(remoteWebElement.getAttribute("scrollable")));
    this.setCheckable(Boolean.valueOf(remoteWebElement.getAttribute("checkable")));
    this.setPackageName(remoteWebElement.getAttribute("package"));
    log.debug(this);
  }

  public MobileWebElement(Node node, Integer depth, Platform platform, String webViewName) {
    this.depth = depth;
    this.platform = platform;
    this.uuid = UUID.randomUUID().toString();
    this.setTagName(node.getNodeName());
    this.setWebViewName(webViewName);
    NamedNodeMap attributes = node.getAttributes();
    Map<String, Object> attributesMap = new HashMap<>();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attribute = attributes.item(i);
      switch (attribute.getNodeName()) {
        case "data-testsigma-inspector-width":
          this.setWidth(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "data-testsigma-inspector-height":
          this.setHeight(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "data-testsigma-inspector-x":
          this.setX1(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "data-testsigma-inspector-y":
          this.setY1(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "id":
          this.setId(attribute.getNodeValue());
          attributesMap.put(attribute.getNodeName(), attribute.getNodeValue());
          break;
        default:
          attributesMap.put(attribute.getNodeName(), attribute.getNodeValue());
          break;
      }
    }
    if ((this.getX2() == null) && (this.getWidth() != null) && (this.getX1() != null)) {
      this.setX2(this.getX1() + this.getWidth());
    }
    if ((this.getY2() == null) && (this.getHeight() != null) && (this.getY1() != null)) {
      this.setY2(this.getY1() + this.getHeight());
    }
    this.setAttributes(attributesMap);
    if (node.hasChildNodes()) {
      List<MobileElement> elements = new ArrayList<>();
      NodeList childNodes = node.getChildNodes();

      for (int j = 0; j < childNodes.getLength(); j++) {
        Node childNode = childNodes.item(j);
        if (childNode.hasAttributes()) {
          MobileWebElement element = new MobileWebElement(childNode, (this.depth + 1), this.platform, this.webViewName);
          element.setParent(this);
          elements.add(element);
        }
      }
      setChildElements(elements);
    }
  }

  public String getType() {
    return this.getTagName();
  }

  @Override
  protected String getXpathByUniqueIds(MobileElement mobileElement) {
    String xPathValue = null;

    if (StringUtils.isNotBlank(mobileElement.getId()) && !StringUtils.equalsIgnoreCase("undefined", mobileElement.getId())) {
      xPathValue = "//" + mobileElement.getType() + "[@id=\"" + mobileElement.getId() + "\"]";
    }
    return xPathValue;
  }
}
