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
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.remote.RemoteWebElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Log4j2
public class MobileElement {
  private static final String ANDROID_ROOT_ELEMENT = "hierarchy";
  private static final String IOS_ROOT_ELEMENT = "AppiumAUT";

  protected String id;
  protected String uuid;
  protected String name;
  protected String xpath;
  protected Integer x1;
  protected Integer y1;
  protected Integer x2;
  protected Integer y2;
  protected Platform platform;
  protected Integer width;
  protected Integer height;
  protected Integer depth;
  protected String webViewName;
  protected String tagName;
  private String value;
  private String type;
  private Boolean enabled;
  private Boolean visible;
  private Integer index;
  private String contentDesc;
  private String resourceId;
  private Boolean password;
  private Boolean clickable;
  private Boolean checked;
  private Boolean longClickable;
  private Boolean selected;
  private Boolean scrollable;
  private Boolean checkable;
  private Boolean focusable;
  private String text;
  private String packageName;
  private String label;
  private Boolean valid;
  private String accessibilityId;
  private Map<String, Object> attributes;
  private Set<String> contextNames;
  public boolean optimiseXpath;

  @ToString.Exclude
  private MobileElement parent;

  private List<MobileElement> childElements;

  private List<MobileWebElement> webViewElements;

  public MobileElement() {

  }

  public MobileElement(RemoteWebElement remoteWebElement, Platform platform) {
    this.populateAttributes(remoteWebElement);
    Rectangle rectangle = remoteWebElement.getRect();
    setBounds(rectangle);
    if (Platform.Android.equals(platform)) {
      this.populateAndroidAttributes(remoteWebElement);
    } else {
      this.populateIosAttributes(remoteWebElement);
    }
    populateXpath();
  }

  public MobileElement(Node node, Integer depth, Platform platform) {
    this.depth = depth;
    this.platform = platform;
    this.uuid = UUID.randomUUID().toString();
    Matcher matcher;
    NamedNodeMap attributes = node.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attribute = attributes.item(i);
      switch (attribute.getNodeName()) {
        case "name":
          this.setName(attribute.getNodeValue());
          if (Platform.iOS.equals(this.platform)) {
            this.setId(attribute.getNodeValue());
            this.setAccessibilityId(attribute.getNodeValue());
          }
          break;
        case "value":
          this.setValue(attribute.getNodeValue());
          break;
        case "class":
        case "type":
          this.setType(attribute.getNodeValue());
          break;
        case "enabled":
          this.setEnabled(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "visible":
          this.setVisible(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "password":
          this.setPassword(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "clickable":
          this.setClickable(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "checked":
          this.setChecked(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "longClickable":
          this.setLongClickable(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "selected":
          this.setSelected(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "scrollable":
          this.setScrollable(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "checkable":
          this.setCheckable(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "content-desc":
          this.setContentDesc(attribute.getNodeValue());
          if (Platform.Android.equals(this.platform)) {
            this.setAccessibilityId(attribute.getNodeValue());
          }
          break;
        case "accessibility-id":
          this.setAccessibilityId(attribute.getNodeValue());
          break;
        case "text":
          this.setText(attribute.getNodeValue());
          break;
        case "package":
          this.setPackageName(attribute.getNodeValue());
          break;
        case "resource-id":
          this.setResourceId(attribute.getNodeValue());
          if (Platform.Android.equals(this.platform)) {
            this.setId(attribute.getNodeValue());
          }
          break;
        case "index":
          this.setIndex(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "bounds":
          if ((matcher = Pattern.compile("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]").matcher(attribute.getNodeValue())).find()) {
            this.setX1(Integer.parseInt(matcher.group(1)));
            this.setY1(Integer.parseInt(matcher.group(2)));
            this.setX2(Integer.parseInt(matcher.group(3)));
            this.setY2(Integer.parseInt(matcher.group(4)));
          }
          break;
        case "valid":
          this.setValid(Boolean.valueOf(attribute.getNodeValue()));
          break;
        case "label":
          this.setLabel(attribute.getNodeValue());
          break;
        case "x":
          this.setX1(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "y":
          this.setY1(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "width":
          this.setWidth(Integer.valueOf(attribute.getNodeValue()));
          break;
        case "height":
          this.setHeight(Integer.valueOf(attribute.getNodeValue()));
          break;
      }
    }
    if (this.getType() == null) {
      this.setType(node.getNodeName());
    }
    if ((this.getX2() == null) && (this.getWidth() != null) && (this.getX1() != null)) {
      this.setX2(this.getX1() + this.getWidth());
    }
    if ((this.getY2() == null) && (this.getHeight() != null) && (this.getY1() != null)) {
      this.setY2(this.getY1() + this.getHeight());
    }
    if (node.hasChildNodes()) {
      List<MobileElement> elements = new ArrayList<>();
      NodeList childNodes = node.getChildNodes();

      for (int j = 0; j < childNodes.getLength(); j++) {
        Node childNode = childNodes.item(j);
        if (childNode.hasAttributes()) {
          MobileElement element = new MobileElement(childNode, (this.depth + 1), this.platform);
          element.setParent(this);
          elements.add(element);
        }
      }
      setChildElements(elements);
    }
  }

  private void populateIosAttributes(RemoteWebElement remoteWebElement) {
    this.setName(remoteWebElement.getAttribute("name"));
    this.setId(name);
    this.setAccessibilityId(name);
    this.setType(remoteWebElement.getAttribute("type"));
    this.setLabel(remoteWebElement.getAttribute("label"));
  }

  private void populateAndroidAttributes(RemoteWebElement remoteWebElement) {
    this.setName(remoteWebElement.getTagName());
    this.setType(remoteWebElement.getAttribute("class"));
    this.setResourceId(remoteWebElement.getAttribute("resource-id"));
    this.setId(resourceId);
    this.setContentDesc(remoteWebElement.getAttribute("content-desc"));
    this.setAccessibilityId(this.contentDesc);
    this.setPassword(Boolean.valueOf(remoteWebElement.getAttribute("password")));
    this.setClickable(Boolean.valueOf(remoteWebElement.getAttribute("clickable")));
    this.setChecked(Boolean.valueOf(remoteWebElement.getAttribute("checked")));
    this.setLongClickable(Boolean.valueOf(remoteWebElement.getAttribute("longClickable")));
    this.setScrollable(Boolean.valueOf(remoteWebElement.getAttribute("scrollable")));
    this.setCheckable(Boolean.valueOf(remoteWebElement.getAttribute("checkable")));
    this.setPackageName(remoteWebElement.getAttribute("package"));
  }

  private void populateAttributes(RemoteWebElement remoteWebElement) {
    this.setEnabled(remoteWebElement.isEnabled());
    this.setVisible(remoteWebElement.isDisplayed());
    try {
      this.setSelected(remoteWebElement.isSelected());
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception);
    }
    this.setText(remoteWebElement.getText());
    this.setWidth(remoteWebElement.getSize().getWidth());
    this.setHeight(remoteWebElement.getSize().getHeight());
    this.setId(remoteWebElement.getId());
  }

  private void setBounds(Rectangle rectangle) {
    this.setX1(rectangle.getX());
    this.setX2(this.x1 + this.width);
    this.setY1(rectangle.getY());
    this.setY2(this.y1 + this.height);
  }

  public void populateXpath() {
    try {
      String xPathVal = getXpathByUniqueIds(this);

      if (xPathVal != null) {
        setXpath(xPathVal);
      } else {
        StringBuilder xPathValue = new StringBuilder("/").append(getType());
        MobileElement parentElement = getParent();
        if (parentElement != null) {
          if (parentElement.getXpath().equals("/body"))
            xPathValue = new StringBuilder("/" + parentElement.getXpath()).append(xPathValue);
          else
            xPathValue = new StringBuilder(parentElement.getXpath()).append(xPathValue);
          if (parentElement.getChildElements().size() > 1) {
            Integer childIndex = getChildIndex();
            if (childIndex != 0) {
              xPathValue.append("[").append(childIndex).append("]");
            }
          }
        }
        setXpath(xPathValue.toString());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  protected String getXpathByUniqueIds(MobileElement mobileElement) {
    String xPathValue = null;

    if (StringUtils.isNotBlank(mobileElement.getContentDesc())) {
      xPathValue = "//" + mobileElement.getType() + "[@content-desc=\"" + mobileElement.getContentDesc() + "\"]";
    } else if (StringUtils.isNotBlank(mobileElement.getResourceId()) && !StringUtils.equalsIgnoreCase("undefined", mobileElement.getResourceId())) {
      xPathValue = "//" + mobileElement.getType() + "[@resource-id=\"" + mobileElement.getResourceId() + "\"]";
    } else if (StringUtils.isNotBlank(mobileElement.getName())) {
      xPathValue = "//" + mobileElement.getType() + "[@name=\"" + mobileElement.getName() + "\"]";
    }
    return xPathValue;
  }

  protected Integer getChildIndex() {
    List<MobileElement> matchingChildElements = getParent().getChildElements().stream().filter(
      element -> element.getType().equalsIgnoreCase(this.getType())).collect(Collectors.toList());
    if (matchingChildElements.size() > 1) {
      int index = 0;
      for (MobileElement mobileElement : matchingChildElements) {
        if (mobileElement.getUuid().equals(this.uuid))
          break;
        index++;
      }
      return (index + 1);
    } else {
      return 0;
    }
  }


}
