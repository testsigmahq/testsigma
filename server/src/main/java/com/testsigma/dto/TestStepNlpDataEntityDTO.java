package com.testsigma.dto;

import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.model.AddonTestStepTestData;
import lombok.Data;

@Data
public class TestStepNlpDataEntityDTO {
    private String type;
    private String value;
    private DefaultDataGeneratorsEntity testDataFunction;
    private AddonTestStepTestData addonTDF;
}
