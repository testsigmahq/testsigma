package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LingeredTestsDTO {
    private Long testCaseId;
    private Long duration;
    private ResultConstant result;
}
