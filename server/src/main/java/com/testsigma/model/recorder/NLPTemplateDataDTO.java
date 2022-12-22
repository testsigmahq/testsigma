package com.testsigma.model.recorder;

import lombok.Data;

import java.util.Map;

@Data
public class NLPTemplateDataDTO {
    public Map<String, String> testData;
    public String uiIdentifier;
    public String attribute;
    public String fromUiIdentifier;
    public String toUiIdentifier;
}
