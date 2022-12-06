package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.exceptions.AutomatorException;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

public class WaitUntilAjaxCallIsCompletedAction extends ElementAction {

    private static final String SUCCESS_MESSAGE = "Successfully waited until the ajax calls are completed.";
    private static final String FAILURE_MESSAGE = "Ajax call is not completely loaded in given wait time, you may try increasing step timeout." +
            " Waited <b>%s</b> seconds for ajax calls to complete.";

    @Override
    public void execute() throws Exception {
        try {
            boolean pageLoaded = getWebDriverWait().until(CustomExpectedConditions.waitForAjaxCallsUsingJS());
            Assert.isTrue(pageLoaded, String.format(FAILURE_MESSAGE, getTimeout()));
            setSuccessMessage(SUCCESS_MESSAGE);
        } catch (TimeoutException e) {
            throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
        }
    }

}
