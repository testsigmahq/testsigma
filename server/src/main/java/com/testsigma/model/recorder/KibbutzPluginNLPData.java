package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.model.AddonElementData;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KibbutzPluginNLPData {
    private Map<String, AddonElementData> uiIdentifiers;
    private Map<String, KibbutzTestStepTestData> testData;
    //private Map<String, KibbutzEnvironmentData> environmentData;
    //private Map<String, KibbutzTestDataProfile> testDataProfile;
    //private Map<String, KibbutzTestDataSet> testDataSet;
}
