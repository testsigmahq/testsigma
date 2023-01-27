package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlakyTestsDTO {
    private Long id;
    private Long testCaseId;
    private Long testPlanResultId;
    private ResultConstant result;
    private String message;
}
