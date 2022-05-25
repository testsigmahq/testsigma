package com.testsigma.automator.actions.web.store;


import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.exceptions.AutomatorException;

import java.lang.reflect.InvocationTargetException;

public class StoreTestDataProxyNlpSnippet extends ElementAction {
  protected static final String FAILURE_MESSAGE = "Unable to Perform Store Action due to " +
    "invalid selectable test data. It accepts only Name/SetIndex/SetName/etc. provided in the list";

  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case NaturalTextActionConstants.TEST_DATA_NAME:
        StoreCurrentTestDataProfileNameNlpSnippet presence = (StoreCurrentTestDataProfileNameNlpSnippet) this.initializeChildSnippet(StoreCurrentTestDataProfileNameNlpSnippet.class);
        presence.execute();
        this.setSuccessMessage(presence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.SET_INDEX:
        StoreCurrentTestDatasetIndexNlpSnippet absence = (StoreCurrentTestDatasetIndexNlpSnippet) this.initializeChildSnippet(StoreCurrentTestDatasetIndexNlpSnippet.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.SET_NAME:
        StoreCurrentTestDataSetNameNlpSnippet notDisplayed = (StoreCurrentTestDataSetNameNlpSnippet) this.initializeChildSnippet(StoreCurrentTestDataSetNameNlpSnippet.class);
        notDisplayed.execute();
        this.setSuccessMessage(notDisplayed.getSuccessMessage());
        break;
      default:
        setErrorMessage(FAILURE_MESSAGE);
        throw new AutomatorException(FAILURE_MESSAGE);
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
    snippet.setTimeout(this.getTimeout());
    snippet.setGlobalElementTimeOut(this.getGlobalElementTimeOut());
    snippet.setActualValue(this.getActualValue());
    snippet.setAdditionalData(this.getAdditionalData());
    snippet.setTestDataProfileEntity(this.getTestDataProfileEntity());
    return snippet;

  }
}
