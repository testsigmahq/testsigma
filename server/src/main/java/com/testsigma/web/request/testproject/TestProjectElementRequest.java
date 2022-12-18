package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectElementRequest {
    private String id;
    private String name;
    private List<TestProjectElementLocatorRequest> locators;
}
