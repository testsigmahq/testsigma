package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class AddonPluginTestDataFunctionParameterEntity {
  private Long id;
  private String name;
  private String description;
  private Long addonId;
  private String reference;
}

