package com.testsigma.automator.actions.web.scroll;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ActionsAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;

@Log4j2
public class ScrollInsideElementProxyAction extends ActionsAction {


    @Override
    public void execute() throws Exception {
      String status = getTestData(NaturalTextActionConstants.TEST_DATA_TOP_BOTTOM_KEY);
      switch (status) {
        case ActionConstants.TOP:
          ScrollInsideElementToTopAction top = (ScrollInsideElementToTopAction) this.initializeChildSnippet(ScrollInsideElementToTopAction.class);
          top.execute();
          this.setSuccessMessage(top.getSuccessMessage());
          break;
        case ActionConstants.BOTTOM:
          ScrollInsideElementToBottomAction bottom = (ScrollInsideElementToBottomAction) this.initializeChildSnippet(ScrollInsideElementToBottomAction.class);
          bottom.execute();
          this.setSuccessMessage(bottom.getSuccessMessage());
          break;
        default:
          setErrorMessage("Unable to Perform Scroll Action due to error at test data");
          throw new AutomatorException("Unable to Perform Scroll Action due to error at test data");
      }
    }


    protected Object initializeChildSnippet(Class<?> snippetClassName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      ActionsAction snippet = (ActionsAction) snippetClassName.getDeclaredConstructor().newInstance();
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
