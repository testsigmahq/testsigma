package com.testsigma.web.request;

import lombok.Data;

import java.util.List;

@Data
public class XrayGraphQLVariables {
    private String issueId;
    private List<String> environments;
}
