package com.testsigma.model;

import lombok.Data;

import java.util.Map;

@Data
public class TestStepData {

    private Map<String, TestStepNlpData> testData;
}
