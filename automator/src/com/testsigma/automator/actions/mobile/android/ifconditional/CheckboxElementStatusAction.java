package com.testsigma.automator.actions.mobile.android.ifconditional;


import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.android.verify.MobileNativeVerifyCheckedAction;
import com.testsigma.automator.actions.mobile.android.verify.MobileNativeVerifyUnCheckedAction;

import java.lang.reflect.InvocationTargetException;


public class CheckboxElementStatusAction extends ElementAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.CHECKED:
        MobileNativeVerifyCheckedAction checked = (MobileNativeVerifyCheckedAction) this.initializeChildSnippet(MobileNativeVerifyCheckedAction.class);
        checked.execute();
        this.setSuccessMessage(checked.getSuccessMessage());
        break;
      case ActionConstants.UN_CHECKED:
        MobileNativeVerifyUnCheckedAction unChecked = (MobileNativeVerifyUnCheckedAction) this.initializeChildSnippet(MobileNativeVerifyUnCheckedAction.class);
        unChecked.execute();
        this.setSuccessMessage(unChecked.getSuccessMessage());
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
