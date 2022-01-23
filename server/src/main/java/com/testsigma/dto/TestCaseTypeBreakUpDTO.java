package com.testsigma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCaseTypeBreakUpDTO {
  Long type;
  Long count;
}
