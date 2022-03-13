package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SelectMultipleOptionByTextAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected multiple options.";
  private static final String ELEMENTS_MISMATCHED = "Cannot select all options from given test data <b>\"%s\"</b>. " +
    "Following are the selected options in element with given locator <b>\"%s:%s\"</b>. <br> " +
    "Selected options: %s";
  private static final String FAILURE_NOT_SELECTABLE = "Unable to select option with given text <b>\"%s\"</b>." +
    "Please verify the select list contains an option with text <b>\"%s\"</b>. <br>" +
    "Selected options which are matching given test data : %s .";
  private static final String FAILURE_NOT_SELECTABLE_FOR_NULL_ELEMENTS = "Unable to select option with given text <b>\"%s\"</b>." +
    "Please verify the select list contains an option with text <b>\"%s\"</b>. <br>" +
    "None of the elements are selected from given test data.";
  List<String> selectedTextList = new ArrayList<>();

  @Override
  protected void execute() throws Exception {

    findElement();
    Select selectElement = new Select(getElement());
    String[] multipleOptions = getTestData().split(",");
    for (int i = 0; i < multipleOptions.length; i++) {
      String multipleText = multipleOptions[i];
      selectOptionByText(selectElement, multipleText);
      selectedTextList.add(multipleText);

    }
    List<WebElement> webElements = selectElement.getAllSelectedOptions();
    if (webElements.size() < multipleOptions.length) {
      List<String> selectedText = new ArrayList<>();
      for (WebElement webElement : webElements) {
        selectedText.add(webElement.getText());
      }
      throw new AutomatorException(String.format(ELEMENTS_MISMATCHED, getTestData(), getFindByType(), getLocatorValue(), selectedText));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private void selectOptionByText(Select select, String textToSelect) throws AutomatorException {
    try {
      select.selectByVisibleText(textToSelect);
    } catch (Exception e) {
      if (!selectedTextList.isEmpty()) {
        throw new AutomatorException(String.format(FAILURE_NOT_SELECTABLE, textToSelect, textToSelect,
          selectedTextList.toString().replace("[", "").replace("]", "")));
      } else {
        throw new AutomatorException(String.format(FAILURE_NOT_SELECTABLE_FOR_NULL_ELEMENTS, textToSelect, textToSelect));
      }
    }
  }
}
