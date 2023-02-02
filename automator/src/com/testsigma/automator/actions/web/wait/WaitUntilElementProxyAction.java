package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;

import java.lang.reflect.InvocationTargetException;

public class WaitUntilElementProxyAction extends ElementAction {


  @Override
  public void execute() throws Exception {
    String status = getTestData(NaturalTextActionConstants.TEST_DATA_VISIBLE_SELECTED_KEY);
    switch (status) {
      case ActionConstants.VISIBLE:
        WaitUntilElementIsVisibleAction visible = (WaitUntilElementIsVisibleAction) this.initializeChildSnippet(WaitUntilElementIsVisibleAction.class);
        visible.execute();
        this.setSuccessMessage(visible.getSuccessMessage());
        break;
      case ActionConstants.NOT_VISIBLE:
        WaitUntilElementNotVisibleAction notVisible = (WaitUntilElementNotVisibleAction) this.initializeChildSnippet(WaitUntilElementNotVisibleAction.class);
        notVisible.execute();
        this.setSuccessMessage(notVisible.getSuccessMessage());
        break;
      case ActionConstants.SELECTED:
        WaitUntilElementIsSelectedAction selectedNlp = (WaitUntilElementIsSelectedAction) this.initializeChildSnippet(WaitUntilElementIsSelectedAction.class);
        selectedNlp.execute();
        this.setSuccessMessage(selectedNlp.getSuccessMessage());
        break;
      case ActionConstants.NOT_SELECTED:
        WaitUntilElementNotSelectedAction notSelectedNlp = (WaitUntilElementNotSelectedAction) this.initializeChildSnippet(WaitUntilElementNotSelectedAction.class);
        notSelectedNlp.execute();
        this.setSuccessMessage(notSelectedNlp.getSuccessMessage());
        break;
      case ActionConstants.CLICKABLE:
        WaitUntilElementIsClickableAction clickable = (WaitUntilElementIsClickableAction) this.initializeChildSnippet(WaitUntilElementIsClickableAction.class);
        clickable.execute();
        this.setSuccessMessage(clickable.getSuccessMessage());
        break;
      case ActionConstants.ENABLED:
        WaitUntilElementIsEnabledAction enabled = (WaitUntilElementIsEnabledAction) this.initializeChildSnippet(WaitUntilElementIsEnabledAction.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case ActionConstants.DISABLED:
        WaitUntilElementIsDisabledAction disabled = (WaitUntilElementIsDisabledAction) this.initializeChildSnippet(WaitUntilElementIsDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Wait Action due to error at test data");
        throw new AutomatorException("Unable to Perform Wait Action due to error at test data");
    }
  }

  protected Object initializeChildSnippet(Class<?> snippetClassName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    ElementAction snippet = (ElementAction) snippetClassName.getDeclaredConstructor().newInstance();
    snippet.setDriver(this.getDriver());
    snippet.setElement(this.getElement());
    snippet.setElementPropertiesEntityMap(this.getElementPropertiesEntityMap());
    snippet.setTestDataPropertiesEntityMap(this.getTestDataPropertiesEntityMap());
    snippet.setAttributesMap(this.getAttributesMap());
    snippet.setRuntimeDataProvider(this.getRuntimeDataProvider());
    snippet.setEnvSettings(this.getEnvSettings());
    return snippet;

  }
}
