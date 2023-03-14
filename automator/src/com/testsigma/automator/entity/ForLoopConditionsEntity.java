package com.testsigma.automator.entity;

import lombok.Data;

import java.util.Map;

@Data
public class ForLoopConditionsEntity {
    private Long id;

    private Long testCaseId;

    private Long testStepId;

    private Long testDataProfileId;

    private IterationType iterationType;

    private TestDataType leftParamType;

    private String leftParamValue;

    private Operator operator;

    private String rightParamValue;

    private TestDataType rightParamType;

    private Long copiedFrom;

    private Long importedId;

    private String testData;

    private Long leftFunctionId;

    private Long rightFunctionId;

    private LoopDataMapEntity leftDataMap;

    private LoopDataMapEntity rightDataMap;
}
