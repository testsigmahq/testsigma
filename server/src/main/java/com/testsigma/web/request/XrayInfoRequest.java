package com.testsigma.web.request;

import lombok.Data;

@Data
public class XrayInfoRequest {

    private String summary;
    private String description;
    private String user;
    private String testPlanKey;
}
