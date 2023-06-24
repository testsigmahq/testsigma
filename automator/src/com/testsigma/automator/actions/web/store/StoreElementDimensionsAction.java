package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.exceptions.AutomatorException;
import org.openqa.selenium.Rectangle;

public class StoreElementDimensionsAction extends ElementAction {

    protected static final String FAILURE_MESSAGE = "Unable to Perform Store Action due to " +
            "invalid selectable test data. It accepts only xOffset/yOffset/height/width provided in the list";

    private static final String SUCCESS_MESSAGE = "Successfully saved dimension in a run time variable.<br><b>%s=%s</b>";

    @Override
    public void execute() throws Exception {

        findElement();
        Rectangle rect = getElement().getRect();


        String status = getTestData();
        int runTimeVarValue;
        switch (status) {
            case NaturalTextActionConstants.ELEMENT_xOFFSET:
                runTimeVarValue = rect.getX();
                runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue + "");
                resultMetadata.put(getTestData(), runTimeVarValue);
                setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
                break;
            case NaturalTextActionConstants.ELEMENT_yOFFSET:
                runTimeVarValue = rect.getY();
                runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue + "");
                resultMetadata.put(getTestData(), runTimeVarValue);
                setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
                break;
            case NaturalTextActionConstants.ELEMENT_HEIGHT:
                runTimeVarValue = rect.getHeight();
                runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue + "");
                resultMetadata.put(getTestData(), runTimeVarValue);
                setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
                break;
            case NaturalTextActionConstants.ELEMENT_WIDTH:
                runTimeVarValue = rect.getWidth();
                runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue + "");
                resultMetadata.put(getTestData(), runTimeVarValue);
                setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
                break;
            default:
                setErrorMessage(FAILURE_MESSAGE);
                throw new AutomatorException(FAILURE_MESSAGE);
        }
    }

}
