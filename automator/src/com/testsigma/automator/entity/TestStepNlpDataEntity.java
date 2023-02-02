package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class TestStepNlpDataEntity {
    private String type;
    private String value;
    private DefaultDataGeneratorsEntity testDataFunction;
}
