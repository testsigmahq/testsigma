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

    @JsonProperty("condition_if")
    private Object ifConditionExpectedResults;
    @JsonProperty("condition-type")
    private TestStepConditionType conditionType;
    @JsonProperty("test-data")
    private Map<String, TestStepNlpData> testData;
    @JsonProperty("custom-step")
    private TestStepCustomStep customStep;
    @JsonProperty("ui-identifier")
    private String uiIdentifier;
    @JsonProperty("from-ui-identifier")
    private String fromUiIdentifier;
    @JsonProperty("to-ui-identifier")
    private String toUiIdentifier;
    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("for_loop")
    private TestStepForLoop forLoop;
    @JsonProperty("while_loop")
    private TestStepWhileLoop whileLoop;
    @JsonProperty("whileCondition")
    private String whileCondition;
    @JsonProperty("migrated")
    private Boolean migrated = Boolean.FALSE;
}
