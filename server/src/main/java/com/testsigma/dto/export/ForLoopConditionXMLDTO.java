package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.IterationType;
import com.testsigma.model.TestDataType;
import lombok.Data;

@Data
@JsonListRootName(name = "ForLoopConditionsList")
@JsonRootName(value = "ForLoopConditions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForLoopConditionXMLDTO extends BaseXMLDTO {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("TestCaseId")
    private Long testCaseId;

    @JsonProperty("TestStepId")
    private Long testStepId;

    @JsonProperty("TestDataProfileId")
    private Long testDataProfileId;

    @JsonProperty("IterationType")
    private IterationType iterationType;

    @JsonProperty("LeftParamType")
    private TestDataType leftParamType;

    @JsonProperty("LeftParamValue")
    private String leftParamValue;

    @JsonProperty("Operator")
    private Operator operator;

    @JsonProperty("RightParamValue")
    private String rightParamValue;

    @JsonProperty("RightParamType")
    private TestDataType rightParamType;

    @JsonProperty("CopiedFrom")
    private Long copiedFrom;

    @JsonProperty("ImportedId")
    private Long importedId;

    @JsonProperty("TestData")
    private String testData;

    @JsonProperty("LeftFunctionId")
    private Long leftFunctionId;

    @JsonProperty("RightFunctionId")
    private Long rightFunctionId;

    @JsonProperty("LeftDataMap")
    private LoopDataMapDTO leftDataMap;

    @JsonProperty("RightDataMap")
    private LoopDataMapDTO rightDataMap;
}

