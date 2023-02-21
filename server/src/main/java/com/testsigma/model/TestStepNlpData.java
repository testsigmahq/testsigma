package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepNlpData {
    private String value;
    private String type;
    private DefaultDataGeneratorsEntity testDataFunction;
    private AddonTestStepTestData addonTDF;
}
