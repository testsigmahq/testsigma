package com.testsigma.dto;

import lombok.Data;

import java.util.List;

@Data
public class SuggestionDTO {
  List<SuggestionDTO> groups;
  private Long id;
  private Integer order;
  private Integer workspaceType;
  private String snippetClass;
  private String displayName;
  private Integer naturalTextActionId;
}
