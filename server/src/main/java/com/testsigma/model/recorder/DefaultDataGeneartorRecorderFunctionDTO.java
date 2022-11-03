package com.testsigma.model.recorder;

import lombok.Data;

import java.util.Map;

@Data
public class DefaultDataGeneartorRecorderFunctionDTO {

    private Long id;
    private String name;
    private String classPackage;
    private String className;
    private String classDisplayName;
    private String description;
    private String functionName;
    private String binaryFileUrl;
    private Integer lib;
    private Map<String, String> arguments;
    private Map<String, String> argumentTypes;

}
