package com.testsigma.automator.entity;

import com.testsigma.automator.constants.AddonActionParameterType;
import lombok.Data;

@Data
public class AddonNaturalTextActionParameterEntity {
  private Long id;
  private String name;
  private String description;
  private AddonActionParameterType type;
  private Long pluginActionId;
  private String reference;
}
