package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectStepParameter {
    private String direction;
    private String name;
    private String value;
}
