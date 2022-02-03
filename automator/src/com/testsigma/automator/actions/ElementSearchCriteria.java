/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions;

import com.testsigma.automator.exceptions.AutomatorException;
import io.appium.java_client.MobileBy;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.openqa.selenium.By;

@Data
@AllArgsConstructor
public class ElementSearchCriteria {
  private FindByType findByType;
  private String byValue;

  public By getBy() throws AutomatorException {
    switch (findByType.ordinal()) {
      case 0:
        return By.id(this.getByValue());
      case 1:
        return By.name(this.getByValue());
      case 2:
        return By.className(this.getByValue());
      case 3:
        return By.cssSelector(this.getByValue());
      case 4:
        return By.tagName(this.getByValue());
      case 5:
        return By.xpath(this.getByValue());
      case 6:
        return new MobileBy.ByAccessibilityId(this.getByValue());
      case 7:
        return By.linkText(this.getByValue());
      case 8:
        return By.partialLinkText(this.getByValue());
    }
    throw new AutomatorException("Unknown find by type");
  }

  public String toString() {
    return "Find by: " + findByType + ", Value: " + byValue;
  }
}
