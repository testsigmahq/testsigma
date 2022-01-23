package com.testsigma.automator.entity;

import com.testsigma.automator.constants.KibbutzActionParameterType;
import lombok.Data;

@Data
public class AddonNaturalTextActionParameterEntity {
  private Long id;
  private String name;
  private String description;
  private KibbutzActionParameterType type;
  private Long pluginActionId;
  private String reference;
}
