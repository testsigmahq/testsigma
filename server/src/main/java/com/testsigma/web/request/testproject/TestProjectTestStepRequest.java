package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.model.TestStepType;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectTestStepRequest {
    private String id;
    private Boolean enabled;
    private TestProjectStepActionRequest action;
    private String elementId;
    private Long repeat;
    private String type;
    private String targetTestId;
    private TestProjectStepSettings settings;
    private List<TestProjectStepParameter> parameterMaps;
    private List<TestProjectStepCondition> conditions;
    private List<TestProjectStepContext> contexts;

    public TestStepType getStepType(){
        switch (this.type){
            case "Action":
            case "Addon":
                return TestStepType.ACTION_TEXT;
            case "Test":
                return TestStepType.STEP_GROUP;
            default:
                return null;
        }
    }

}
