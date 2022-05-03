package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Entry {
  @JsonProperty("Key")
  private String key;
  @JsonProperty("Value")
  private Object value;
}
