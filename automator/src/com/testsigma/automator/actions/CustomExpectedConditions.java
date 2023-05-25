package com.testsigma.automator.actions;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class CustomExpectedConditions {

  public static ExpectedCondition<List<WebElement>> allElementsAreEnabled(
    final By locator) {
    return new ExpectedCondition<List<WebElement>>() {
      @Override
      public List<WebElement> apply(WebDriver driver) {
        List<WebElement> elements = driver.findElements(locator);
        for (WebElement element : elements) {
          if (!element.isEnabled()) {
            return null;
          }
        }
        return elements.size() > 0 ? elements : null;
      }

      @Override
      public String toString() {
        return "Until all elements are enabled, elements located by " + locator;
      }
    };
  }

  public static ExpectedCondition<Boolean> elementIsEnabled(final By by) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.findElement(by).isEnabled();
      }

      public String toString() {
        return "state of element located by " + by.toString();
      }
    };
  }

  public static ExpectedCondition<Boolean> elementIsDisabled(final By by) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return !(driver.findElement(by).isEnabled());
      }

      public String toString() {
        return "state of element located by " + by.toString();
      }
    };
  }

  public static ExpectedCondition<Boolean> textToBePresent(final String text) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          String elementText = driver.findElement(By.tagName("body")).getText();
          return elementText.contains(text);
        } catch (StaleElementReferenceException e) {
          return false; // return null is changed to return false// TODO::
        }
      }

      public String toString() {
        return "state of text located by ";//+ toString();
      }
    };
  }

  public static ExpectedCondition<Boolean> mobileTextToBePresent(final String text) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          String elementText = driver.getPageSource();
          return elementText.contains(text);
        } catch (StaleElementReferenceException e) {
          return false; // return null is changed to return false// TODO::
        }
      }

      public String toString() {
        return "state of text located by ";//+ toString();
      }
    };
  }

  public static ExpectedCondition<Boolean> waitForPageLoadUsingJS() {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          Object readyState = ((JavascriptExecutor) driver).executeScript("return document.readyState;");
          return readyState.toString().equalsIgnoreCase("complete");
        } catch (UnreachableBrowserException e) {
          return false; // return null is changed to return false// TODO::
        }
      }
    };
  }

  public static ExpectedCondition<Boolean> waitForAjaxCallsUsingJS() {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          Object jQueryState = ((JavascriptExecutor) driver).executeScript("return jQuery.active");
          return jQueryState.toString().equalsIgnoreCase("0");
        } catch (UnreachableBrowserException e) {
          return false; // return null is changed to return false// TODO::
        }
      }
    };
  }

  public static ExpectedCondition<Boolean> downloadToBeCompletedInChrome(String javaScriptCode) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> webElements = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(javaScriptCode);
          //If any download is in progress/paused list of webelements will be returned.
          return webElements == null || webElements.size() <= 0;
        } catch (UnreachableBrowserException e) {
          return false;
        }
      }
    };
  }

  public static ExpectedCondition<Boolean> newWindowtobePresent(final int windowCount) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          // int windowsSize = Integer.parseInt(windowCount);
          Set<String> getWindowHandles = driver.getWindowHandles();
          return getWindowHandles.size() > windowCount;
        } catch (NoSuchWindowException e) {
          return false; // return null is changed to return false// TODO::
        }
      }
    };
  }

  // TODO:: This method needs to be rewritten to return true/false
  public static ExpectedCondition<List<WebElement>> allElementsOfTagnameAreDisplayed(final String tagname) {
    return new ExpectedCondition<List<WebElement>>() {
      public List<WebElement> apply(WebDriver driver) {
        List<WebElement> allElementsOfTagname = driver.findElements(By.tagName(tagname));
        for (WebElement element : allElementsOfTagname) {
          if (!element.isDisplayed()) {
            return null;
          }
        }
        return allElementsOfTagname.size() > 0 ? allElementsOfTagname : null;
      }
    };
  }

  public static ExpectedCondition<List<WebElement>> allElementsOfClassNameAreDisplayed(final String classname) {
    return new ExpectedCondition<List<WebElement>>() {
      public List<WebElement> apply(WebDriver driver) {
        List<WebElement> allElementsOfClassName = driver.findElements(By.className(classname));
        for (WebElement element : allElementsOfClassName) {
          if (!element.isDisplayed()) {
            return null;
          }
        }
        return allElementsOfClassName.size() > 0 ? allElementsOfClassName : null;
      }
    };
  }

  public static ExpectedCondition<Boolean> propertytobeChanged(final By by, final String attribute,
                                                               final String oldValue) {

    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return (driver.findElement(by).getAttribute(attribute)).equals(oldValue) == false;
      }

      public String toString() {
        return "state of text located by " + by.toString();
      }
    };
  }

  public static ExpectedCondition<Boolean> classtobeChanged(final By by, final String oldValue) {

    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.findElement(by).getAttribute("class").equals(oldValue) == false;
      }
    };
  }

  final static public void explicitWait(WebDriver driver, By by, Integer wait) {

    if (wait == null || wait < 1 || wait > 120) {
      return;
    }
    if (by != null) {
     new FluentWait<>(driver).withTimeout(Duration.ofSeconds(wait)).pollingEvery(Duration.ofSeconds(10)).ignoring(NoSuchElementException.class);
    }
  }
}
