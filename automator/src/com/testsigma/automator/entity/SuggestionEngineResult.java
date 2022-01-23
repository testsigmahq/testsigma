package com.testsigma.automator.entity;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuggestionEngineResult {
  private Integer id;
  private SuggestionSnippetResult result;
  private String message;
  private SuggestionEngineResultMetaData metaData;
  private Integer suggestionId;

  public SuggestionEngineResult(SuggestionSnippetResult result, String message) {
    this.result = result;
    this.message = message;
  }

  public SuggestionEngineResult(SuggestionSnippetResult result) {
    this.result = result;
  }

}
