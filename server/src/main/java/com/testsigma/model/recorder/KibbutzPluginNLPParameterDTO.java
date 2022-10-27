package com.testsigma.model.recorder;

import com.testsigma.model.AddonActionParameterType;
import lombok.Data;

@Data
public class KibbutzPluginNLPParameterDTO {
    private Long id;
    private String name;
    private String reference;
    private String description;
    private AddonActionParameterType type;
    private String[] allowedValues;
}
