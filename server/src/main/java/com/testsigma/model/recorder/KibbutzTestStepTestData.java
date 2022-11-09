package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.model.TestDataType;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KibbutzTestStepTestData {
    private TestDataType type;
    private String value;
    private Map<String, String> testDataFunctionArguments;
    private Long testDataFunctionId;
    private Boolean isKibbutzFn;
}
