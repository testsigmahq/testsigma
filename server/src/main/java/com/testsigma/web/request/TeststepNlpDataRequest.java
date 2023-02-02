package com.testsigma.web.request;

import lombok.Data;

@Data
public class TeststepNlpDataRequest {
    private String type;
    private String value;
    private TestDataFunctionEntityRequest testDataFunction;
}
