package com.testsigma.automator.actions.web.select;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SelectMultipleOptionByIndexAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully Selected multiple option.";
  private static final String ELEMENTS_MISMATCHED = "Cannot select all options from given test data <b>\"%s\"</b>. " +
    "Following are the selected index with given locator <b>\"%s:%s\"</b>. <br> " +
    "Selected options: %s";
  private static final String ELEMENT_IS_NOT_A_NUMBER = "Given index is not a number, please provide COMMA separated index numbers in test data.";
  private static final String FAILURE_NOT_SELECTABLE = "Unable to select option at given index <b>\"%s\"</b>. " +
    "Please verify the select list contains an option at index <b>\"%s\"</b>. <br> " +
    "Selected options which are matching given test data : %s .";
  private static final String FAILURE_NOT_SELECTABLE_FOR_NULL_ELEMENTS = "Unable to select option at given index <b>\"%s\"</b>. " +
    "Please verify the select list contains an option at index <b>\"%s\"</b>. <br> " +
    "None of indexes are selected from given test data.";
  List<Integer> selectedIndexList = new ArrayList<>();

  @Override
  protected void execute() throws Exception {

    findElement();
    Select selectElement = new Select(getElement());
    List<Integer> multipleOptions = getIntegerArray();
    for (int i = 0; i < multipleOptions.size(); i++) {
      Integer multipleValue = multipleOptions.get(i);
      selectByIndex(selectElement, multipleValue);
      selectedIndexList.add(multipleValue);
    }
    List<WebElement> webElements = selectElement.getAllSelectedOptions();
    if (webElements.size() < multipleOptions.size()) {
      List<String> selectedIndex = new ArrayList<>();
      for (WebElement webElement : webElements) {
        selectedIndex.add(webElement.getText());
      }
      throw new AutomatorException(String.format(ELEMENTS_MISMATCHED, getTestData(), getFindByType(), getLocatorValue(), selectedIndex));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private List<Integer> getIntegerArray() throws AutomatorException {
    String[] multipleOptions = getTestData().split(",");
    List<Integer> indexList = new ArrayList<>();
    for (String index : multipleOptions) {
      indexList.add(NumberFormatter.getIntegerValue(index, ELEMENT_IS_NOT_A_NUMBER));
    }
    return indexList;
  }

  private void selectByIndex(Select select, int elementToSelect) throws AutomatorException {
    try {
      select.selectByIndex(elementToSelect);
    } catch (Exception e) {
      if (!selectedIndexList.isEmpty()) {
        throw new AutomatorException(String.format(FAILURE_NOT_SELECTABLE, elementToSelect, elementToSelect,
          selectedIndexList.toString().replace("[", "").replace("]", "")));
      } else {
        throw new AutomatorException(String.format(FAILURE_NOT_SELECTABLE_FOR_NULL_ELEMENTS, elementToSelect, elementToSelect));
      }
    }
  }
}
