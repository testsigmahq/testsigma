package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectYamlRequest {
    private String projectId;
    private String projectName;
    private Timestamp created;
    private String owner;
    //TestCases
    private List<TestProjectTestCaseRequest> tests;
    //StepGroups
    private List<TestProjectTestCaseRequest> auxTests;
    //Elements
    private List<TestProjectElementRequest> elements;
    private List<TestProjectGlobalParametersRequest> projectParameters;
}
