package com.testsigma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.Header;

@Data
@AllArgsConstructor
public class RestStepResponseDTO {
  private Integer status;
  private String contentStr;
  private Header[] headers;
}
