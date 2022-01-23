package com.testsigma.automator.suggestion.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuggestionEntity {
  private Integer id;
  private Integer order;
  private Integer workspaceType;
  private String snippetClass;
  private String displayName;
  private Integer naturalTextActionId;
  private List<SuggestionEntity> groups = new ArrayList<SuggestionEntity>();
}
