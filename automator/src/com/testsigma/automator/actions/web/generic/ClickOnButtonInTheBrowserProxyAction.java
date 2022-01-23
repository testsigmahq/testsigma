package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.DriverAction;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.common.NavigateBackAction;
import com.testsigma.automator.actions.common.NavigateForwardAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;

@Log4j2
public class ClickOnButtonInTheBrowserProxyAction extends ElementAction {
    @Override
    public void execute() throws Exception {
      String button = getTestData();
      switch (button) {
        case ActionConstants.REFRESH:
          ReLoadCurrentPageAction refresh = (ReLoadCurrentPageAction) this.initializeChildSnippet(ReLoadCurrentPageAction.class);
          refresh.execute();
          this.setSuccessMessage(refresh.getSuccessMessage());
          break;
        case ActionConstants.BACK:
          NavigateBackAction back = (NavigateBackAction) this.initializeChildSnippet(NavigateBackAction.class);
          back.execute();
          this.setSuccessMessage(back.getSuccessMessage());
          break;
        case ActionConstants.FORWARD:
          NavigateForwardAction forward = (NavigateForwardAction) this.initializeChildSnippet(NavigateForwardAction.class);
          forward.execute();
          this.setSuccessMessage(forward.getSuccessMessage());
          break;
        default:
          setErrorMessage("Unable to Click on Button in the Browser due to error at test data");
          throw new AutomatorException("Unable to Click on Button in the Browser due to error at test data");
      }
    }
  protected Object initializeChildSnippet(Class<?> snippetClassName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    DriverAction snippet = (DriverAction) snippetClassName.getDeclaredConstructor().newInstance();
    snippet.setDriver(this.getDriver());
    return snippet;

  }
  }
