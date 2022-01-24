package com.testsigma.automator.actions.mobile.mobileweb.ifconditional;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyDisabledAction;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyElementEnabledAction;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyElementIsNotDisplayedAction;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyElementPresentAction;

import java.lang.reflect.InvocationTargetException;

public class IfElementProxyAction extends ElementAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.ENABLED:
        VerifyElementEnabledAction enabled = (VerifyElementEnabledAction) this.initializeChildSnippet(VerifyElementEnabledAction.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case ActionConstants.DISABLED:
        VerifyDisabledAction disabled = (VerifyDisabledAction) this.initializeChildSnippet(VerifyDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      case ActionConstants.VISIBLE:
        VerifyElementPresentAction visible = (VerifyElementPresentAction) this.initializeChildSnippet(VerifyElementPresentAction.class);
        visible.execute();
        this.setSuccessMessage(visible.getSuccessMessage());
        break;
      case ActionConstants.NOT_VISIBLE:
        VerifyElementIsNotDisplayedAction notVisible = (VerifyElementIsNotDisplayedAction) this.initializeChildSnippet(VerifyElementIsNotDisplayedAction.class);
        notVisible.execute();
        this.setSuccessMessage(notVisible.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Action due to error at test data");
        throw new AutomatorException("Unable to Perform Verify Action due to error at test data");
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
