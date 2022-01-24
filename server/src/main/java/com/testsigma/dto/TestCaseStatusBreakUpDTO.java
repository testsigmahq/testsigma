package com.testsigma.dto;

import com.testsigma.model.TestCaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCaseStatusBreakUpDTO {
  TestCaseStatus status;
  Long count;
}
