package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.dto.export.CloudTestDataFunction;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepNlpData {
    private String type;
    private String value;
    private CloudTestDataFunction testDataFunction;
    private KibbutzTestStepTestData kibbutzTDF;
}
