package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class RunDurationTrendDTO {
    private Long testPlanId;
    private ResultConstant result;
    private Long duration;
    private String buildNo;
    private Timestamp createdDate;
}
