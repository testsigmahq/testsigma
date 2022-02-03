package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;

import java.lang.reflect.InvocationTargetException;

public class VerifyElementProxyAction extends ElementAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case NaturalTextActionConstants.DISPLAYED:
        VerifyElementPresenceAction presence = (VerifyElementPresenceAction) this.initializeChildSnippet(VerifyElementPresenceAction.class);
        presence.execute();
        this.setSuccessMessage(presence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_PRESENT:
      case NaturalTextActionConstants.NOT_VISIBLE:
        VerifyElementAbsenceAction absence = (VerifyElementAbsenceAction) this.initializeChildSnippet(VerifyElementAbsenceAction.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_DISPLAYED:
        VerifyElementNotDisplayedAction notDisplayed = (VerifyElementNotDisplayedAction) this.initializeChildSnippet(VerifyElementNotDisplayedAction.class);
        notDisplayed.execute();
        this.setSuccessMessage(notDisplayed.getSuccessMessage());
        break;
      case NaturalTextActionConstants.AVAILABLE:
      case NaturalTextActionConstants.PRESENT:
      case NaturalTextActionConstants.VISIBLE:
        VerifyElementIsAvailableAction available = (VerifyElementIsAvailableAction) this.initializeChildSnippet(VerifyElementIsAvailableAction.class);
        available.execute();
        this.setSuccessMessage(available.getSuccessMessage());
        break;
      case NaturalTextActionConstants.ENABLED:
      case NaturalTextActionConstants.SELECTED:
        VerifyElementEnabledAction enabled = (VerifyElementEnabledAction) this.initializeChildSnippet(VerifyElementEnabledAction.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case NaturalTextActionConstants.DISABLED:
      case NaturalTextActionConstants.NOT_SELECTED:
        VerifyElementDisabledAction disabled = (VerifyElementDisabledAction) this.initializeChildSnippet(VerifyElementDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      case NaturalTextActionConstants.CHECKED:
        VerifyElementCheckedAction checked = (VerifyElementCheckedAction) this.initializeChildSnippet(VerifyElementCheckedAction.class);
        checked.execute();
        this.setSuccessMessage(checked.getSuccessMessage());
        break;
      case NaturalTextActionConstants.UN_CHECKED:
        VerifyElementUncheckedAction unChecked = (VerifyElementUncheckedAction) this.initializeChildSnippet(VerifyElementUncheckedAction.class);
        unChecked.execute();
        this.setSuccessMessage(unChecked.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Action due to error at test data");
        throw new Exception("Unable to Perform Verify Action due to error at test data");
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
