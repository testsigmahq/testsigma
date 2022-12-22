package com.testsigma.web.request;

import lombok.Data;

@Data
public class XrayResponse {
    private String id;
    private String key;
    private String self;
    private String error;
}
