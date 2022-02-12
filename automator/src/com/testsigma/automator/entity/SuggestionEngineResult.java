package com.testsigma.automator.entity;

import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuggestionEngineResult {
  private Integer id;
  private SuggestionActionResult result;
  private String message;
  private SuggestionEngineResultMetaData metaData;
  private Integer suggestionId;

  public SuggestionEngineResult(SuggestionActionResult result, String message) {
    this.result = result;
    this.message = message;
  }

  public SuggestionEngineResult(SuggestionActionResult result) {
    this.result = result;
  }

}
