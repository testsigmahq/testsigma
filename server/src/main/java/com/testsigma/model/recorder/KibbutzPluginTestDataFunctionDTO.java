package com.testsigma.model.recorder;

import com.testsigma.dto.AddonPluginTestDataFunctionParameterDTO;
import lombok.Data;

import java.util.List;

@Data
public class KibbutzPluginTestDataFunctionDTO {
    private Long id;
    private String fullyQualifiedName;
    private String displayName;
    private String description;
    private Boolean deprecated;
    private Long pluginId;
    private String externalUniqueId;
    private List<AddonPluginTestDataFunctionParameterDTO> parameters;
}

