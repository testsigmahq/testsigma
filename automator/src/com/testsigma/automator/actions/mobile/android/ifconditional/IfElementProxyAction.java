package com.testsigma.automator.actions.mobile.android.ifconditional;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.android.verify.VerifyDisabledSnippet;
import com.testsigma.automator.actions.mobile.android.verify.VerifyElementAbsentSnippet;
import com.testsigma.automator.actions.mobile.android.verify.VerifyElementPresentSnippet;
import com.testsigma.automator.actions.mobile.android.verify.VerifyEnabledSnippet;

import java.lang.reflect.InvocationTargetException;

public class IfElementProxyAction extends ElementAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.ENABLED:
        VerifyEnabledSnippet enabled = (VerifyEnabledSnippet) this.initializeChildSnippet(VerifyEnabledSnippet.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case ActionConstants.DISABLED:
        VerifyDisabledSnippet disabled = (VerifyDisabledSnippet) this.initializeChildSnippet(VerifyDisabledSnippet.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      case ActionConstants.VISIBLE:
        VerifyElementPresentSnippet visible = (VerifyElementPresentSnippet) this.initializeChildSnippet(VerifyElementPresentSnippet.class);
        visible.execute();
        this.setSuccessMessage(visible.getSuccessMessage());
        break;
      case ActionConstants.NOT_VISIBLE:
        VerifyElementAbsentSnippet notVisible = (VerifyElementAbsentSnippet) this.initializeChildSnippet(VerifyElementAbsentSnippet.class);
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
