package com.testsigma.automator.actions;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.springframework.util.Assert;

@Log4j2
public abstract class ActionsAction extends ElementAction {

  private static final String FAILURE_MESSAGE_UNMATCHED_ELEMENT_TYPE = "Element with locator <b>\"%s:%s\"</b> is not pointing to a %s.<br>" +
    "Expected Element Type:\"%s\"<br>Actual Element Type:\"%s\"";

  protected void click(String elementVariableName, boolean retry) throws Exception {
    try {
      findElement(elementVariableName);
      WebElement webElement = getElement();
      Assert.isTrue(webElement.isEnabled(), "Unable to click on the element, Element is in disabled state.");
      if (!webElement.isDisplayed()) {
        log.info("Element is not displayed, trying to scroll and make it visible.");
        scrollToElement(elementVariableName);
        //Get the element after scroll. isDisplayed property may vary after scroll.
        webElement = getDriver().findElement(getBy(elementVariableName));
        if (!webElement.isDisplayed()) {
          String elementType = webElement.getAttribute(ActionConstants.ATTRIBUTE_TYPE);
          Assert.notNull(elementType, "Unable to perform click/check action on element, Element is not displayed.");
          if (elementType.equalsIgnoreCase(ActionConstants.ELEMENT_TYPE_RADIO)
            || elementType.equalsIgnoreCase(ActionConstants.ELEMENT_TYPE_CHECKBOX)) {
            boolean isElementDisplayed = isElementHasViewableDimensions(webElement);
            Assert.isTrue(isElementDisplayed, "Unable to perform click/check action on element, Element is not displayed.");
          } else {
            Assert.isTrue(webElement.isDisplayed(), "Unable to perform click/check action on element, Element is not displayed.");
          }
        }

      }
      webElement.click();
    } catch (AutomatorException e) {
      throw e;
    } catch (WebDriverException ex) {
      if (ex instanceof SessionNotCreatedException || ex instanceof UnreachableBrowserException
        || ex instanceof TimeoutException || ex instanceof NotFoundException) {
        throw ex;
      } else {
        log.error("Encountered exception, doing a retry: " + ex.getMessage(), ex);
        log.error("Encountered exception, doing a retry: " + ex.getMessage(), ex);
        scrollToElement(elementVariableName);
        if (retry) {
          click(elementVariableName, false);
        } else {
          log.info("Retry is false, so using javascript to perform click action");
          clickJavascript(elementVariableName);
        }
      }
    }
  }

  protected void clickJavascript(String elementVariableName) throws Exception {
    log.info("Performing click using javascript function");
    findElement(elementVariableName);
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    js.executeScript("arguments[0].click();", getElement());
    log.info("Click performed using javascript.Element locatorValue - " + getFindByType(elementVariableName)
      + ":" + getLocatorValue(elementVariableName));
  }

  private boolean isElementHasViewableDimensions(WebElement element) {
    String checkIsDisplayed = "try{ if ("
      + " arguments[0].offsetWidth ||"
      + " arguments[0].offsetHeight ||"
      + "arguments[0].getClientRects().length)"
      + "return true;"
      + " else "
      + "return false;"
      + "}catch(e){"
      + "return false;"
      + "}";
    Object result = ((JavascriptExecutor) getDriver()).executeScript(checkIsDisplayed, element);
    return (boolean) result;
  }

  protected void scrollToElement(String elementVariableName) throws Exception {
    findElement(elementVariableName);
    String scrollToElement = "try{ "
      + "arguments[0].scrollIntoView({"
      + " behavior: 'auto', block: 'center', inline: 'center'"
      + "}); return false;"
      + "}catch(e){"
      + "return true;"
      + "}";
    Object result = ((JavascriptExecutor) getDriver()).executeScript(scrollToElement, getElement());

    if (result instanceof Boolean && (Boolean) result) {
      String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, "
        + "window.innerHeight || 0);"
        + "var elementTop = arguments[0].getBoundingClientRect().top;"
        + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

      ((JavascriptExecutor) getDriver()).executeScript(scrollElementIntoMiddle, getElement());
    }
  }

  protected void validateElementType(String targetElementType) {
    String sourceElementType = getElement().getAttribute(ActionConstants.ATTRIBUTE_TYPE);
    Assert.isTrue(sourceElementType!=null,  String.format(FAILURE_MESSAGE_UNMATCHED_ELEMENT_TYPE, getFindByType(), getLocatorValue(),
      targetElementType, targetElementType, sourceElementType));
    Assert.isTrue(sourceElementType.equalsIgnoreCase(targetElementType),
      String.format(FAILURE_MESSAGE_UNMATCHED_ELEMENT_TYPE, getFindByType(), getLocatorValue(),
        targetElementType, targetElementType, sourceElementType));
  }
}
