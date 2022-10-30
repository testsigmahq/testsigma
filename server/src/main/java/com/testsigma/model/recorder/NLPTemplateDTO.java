package com.testsigma.model.recorder;

import com.testsigma.model.StepActionType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NLPTemplateDTO {
    private Long id;
    private WorkspaceType applicationType;
    private String keyword;
    private String grammar;
    private NLPTemplateDataDTO data;
    private String displayName;
    private String action;
    private Boolean deprecated;
    private List allowedValues;
    private StepActionType stepActionType;
}
