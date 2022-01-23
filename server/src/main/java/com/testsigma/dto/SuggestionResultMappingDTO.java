/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.SuggestionResultMetaData;
import com.testsigma.model.SuggestionResultStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class SuggestionResultMappingDTO {
  private Long id;
  private Long stepResultId;
  private Long suggestionId;
  private String message;
  private SuggestionResultStatus result;
  private SuggestionResultMetaData metaData;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
