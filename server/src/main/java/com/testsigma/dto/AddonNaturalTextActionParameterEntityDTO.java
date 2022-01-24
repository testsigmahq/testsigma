package com.testsigma.dto;

import com.testsigma.model.KibbutzActionParameterType;
import lombok.Data;

@Data
public class AddonNaturalTextActionParameterEntityDTO {

  private Long id;
  private String name;
  private String description;
  private KibbutzActionParameterType type;
  private Long pluginActionId;
  private String reference;
}
