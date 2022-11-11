package com.testsigma.web.request;

import lombok.Data;

import java.util.List;

@Data
public class XrayIterationRequest {
    private String name;
    private String status;
    private String log;
    private String duration;
    private List<XrayParameterRequest> parameters;
}