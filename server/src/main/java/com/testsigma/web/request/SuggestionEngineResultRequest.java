/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.SuggestionResultMetaData;
import com.testsigma.model.SuggestionResultStatus;
import lombok.Data;

@Data
public class SuggestionEngineResultRequest {
  private Integer id;
  private Integer suggestionId;
  private SuggestionResultStatus result;
  private String frame;
  private String message;
  private SuggestionResultMetaData metaData;
}
