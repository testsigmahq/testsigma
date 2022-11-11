package com.testsigma.web.request;
import lombok.Data;

@Data
public class GraphqlRequestBody {
    private String query;
    private Object variables;
}
