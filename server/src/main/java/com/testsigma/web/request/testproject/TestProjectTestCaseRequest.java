package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectTestCaseRequest {
    private String id;
    private String name;
    private String platform;
    private TestProjectApplicationRequest application;
    private List<TestProjectTestStepRequest> steps;
    private TestProjectTestCaseSettings settings;
    private List<TestProjectStepParameter> parameters;
}
