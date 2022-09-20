/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class UserPreferenceRequest {
  Long userId;
  String description;
  private Long testCaseFilterId;
  private Long versionId;
  private Long workspaceId;
  @JsonProperty(value = "showedGitHubStar")
  private boolean showedGitHubStar;
  private Long clickedSkipForNow;
}
