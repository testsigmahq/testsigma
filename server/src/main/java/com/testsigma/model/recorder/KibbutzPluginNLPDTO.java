package com.testsigma.model.recorder;

import com.testsigma.model.StepActionType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class KibbutzPluginNLPDTO {
    private Long id;
    private String grammar;
    private String description;
    private WorkspaceType applicationType;
    private Boolean deprecated;
    private List<KibbutzPluginNLPParameterDTO> parameters;
    private StepActionType stepActionType;
}
