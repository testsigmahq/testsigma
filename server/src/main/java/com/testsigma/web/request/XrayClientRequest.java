package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class XrayClientRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;
}
