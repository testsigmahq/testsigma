package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;
import org.apache.commons.lang3.StringUtils;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTSTEP_DATAMAP_KEY_PARAMETER;

public class UpdateTestDataParamWithTestDataAction extends ElementAction {
    private static final String SUCCESS_MESSAGE = "Successfully test-data in a test data parameter <br><b>%s=%s</b> in test data profile %s.";

    private static final String FAILURE_EMPTY_VALUE = "The test-data value for given element is empty." +
            "<br>Updating testdata parameter with empty value may result in unwanted failures in tests where it is being used.";

    private static final String FAILURE_MULTIPLE_LINE_VALUE = "The element " +
            "contains multiple line.<br>Updating testdata parameter with multiple " +
            "line value may result in unwanted failures in tests where it is being " +
            "used.";

    private final String TEST_DATA_PROFILE_ID = "test-data-profile-id";

    private final String TEST_DATA_PROFILE_NAME = "test-data-profile";

    @Override
    public void execute() throws Exception {
        String value = getTestData();

        Assert.isTrue(StringUtils.isNotBlank(value), FAILURE_EMPTY_VALUE);
        Assert.isTrue(!value.contains("\n"),FAILURE_MULTIPLE_LINE_VALUE);

        String testDataName = getTestDataPropertiesEntity(TESTSTEP_DATAMAP_KEY_PARAMETER).getTestDataName();
        String testDataProfileId = getTestDataPropertiesEntity(TEST_DATA_PROFILE_ID).getTestDataValue();
        String testDataProfileName = getTestDataPropertiesEntity(TEST_DATA_PROFILE_NAME).getTestDataValue();
        testDataParams.put(testDataName, value);
        testDataParams.put(TEST_DATA_PROFILE_ID, testDataProfileId);
        setSuccessMessage(String.format(SUCCESS_MESSAGE, testDataName, value, testDataProfileName));
    }
}
