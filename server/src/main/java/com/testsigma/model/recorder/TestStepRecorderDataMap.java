package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepCustomStep;
import com.testsigma.model.TestStepForLoop;
import com.testsigma.model.TestStepWhileLoop;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStepRecorderDataMap {

    @JsonProperty("conditionIf")
    private Object ifConditionExpectedResults;
    @JsonProperty("conditionType")
    private TestStepConditionType conditionType;
    @JsonProperty("testData")
    private Map<String, TestStepNlpData> testData;
    @JsonProperty("customStep")
    private TestStepCustomStep customStep;
    @JsonProperty("uiIdentifier")
    private String uiIdentifier;
    @JsonProperty("fromUiIdentifier")
    private String fromUiIdentifier;
    @JsonProperty("toUiIdentifier")
    private String toUiIdentifier;
    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("forLoop")
    private TestStepRecorderForLoop forLoop;
    @JsonProperty("whileLoop")
    private TestStepWhileLoop whileLoop;
    @JsonProperty("whileCondition")
    private String whileCondition;
    @JsonProperty("migrated")
    private Boolean migrated = Boolean.FALSE;
}
