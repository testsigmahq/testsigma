package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.entity.TestDataType;

public class StoreRuntimeVariableNlpSnippet extends ElementAction {

    private static final String SUCCESS_MESSAGE = "Successfully saved value into %s runtime variable. <br><b>%s=%s</b";

    @Override
    protected void execute() throws Exception {
        String value = getTestData("test-data1");
        if(getTestDataType("test-data1").equals(TestDataType.runtime.toString())) {
            value = runtimeDataProvider.getRuntimeData(value);
        }
        String testData2 = getTestData("test-data2");
        runtimeDataProvider.storeRuntimeVariable(testData2, value);
        resultMetadata.put(testData2, value);
        setSuccessMessage(String.format(SUCCESS_MESSAGE, testData2, testData2, value));
    }
}
