package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class RunDurationTrendDTO {
    private Long testPlanId;
    private ResultConstant result;
    private Long duration;
    private String buildNo;
    private Date createdDate;
}
