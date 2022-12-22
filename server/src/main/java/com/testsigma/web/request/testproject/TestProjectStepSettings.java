package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectStepSettings {
    private Long timeout;
    private String failureBehaviorType;

    public Boolean isIgnoreStep(){
        switch (failureBehaviorType){
            case "Continue":
            case "AlwaysPass":
                return true;
            default:
                return false;

        }
    }
}
