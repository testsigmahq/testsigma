package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectTestCaseSettings {
    private Long stepTimeout;
    //Abort -> Stop, Continue -> ignore, AlwaysPass -> ignore
    private String stepFailureBehaviorType;

    public boolean isIgnoreStep(){
        switch (stepFailureBehaviorType){
            case "Continue":
            case "AlwaysPass":
                return true;
            default:
                return false;

        }
    }
}
