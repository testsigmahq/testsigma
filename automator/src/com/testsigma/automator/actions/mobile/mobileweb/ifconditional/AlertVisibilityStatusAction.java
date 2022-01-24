package com.testsigma.automator.actions.mobile.mobileweb.ifconditional;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyAlertAbsentAction;
import com.testsigma.automator.actions.mobile.mobileweb.verify.VerifyAlertPresentAction;

import java.lang.reflect.InvocationTargetException;

public class AlertVisibilityStatusAction extends ElementAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.NOT_VISIBLE:
        VerifyAlertAbsentAction absence = (VerifyAlertAbsentAction) this.initializeChildSnippet(VerifyAlertAbsentAction.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case ActionConstants.VISIBLE:
        VerifyAlertPresentAction available = (VerifyAlertPresentAction) this.initializeChildSnippet(VerifyAlertPresentAction.class);
        available.execute();
        this.setSuccessMessage(available.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Alert Status Action due to error at test data");
        throw new AutomatorException("Unable to Perform Verify Alert Status due to error at test data");
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
