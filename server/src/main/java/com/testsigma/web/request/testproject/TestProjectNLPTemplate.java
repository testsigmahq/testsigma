package com.testsigma.web.request.testproject;

import lombok.Data;

import java.util.List;

@Data
public class TestProjectNLPTemplate {
    private Long id;
    private String name;
    private String summary;
    private String guid;
    private String description;
    private String className;
    private String type;
    private String source;
    private List labels;
}
