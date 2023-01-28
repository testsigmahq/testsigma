package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailuresByCategoryDTO {
    Long count;
    ResultConstant result;
    String message;
}
