package com.testsigma.model.recorder;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApplicationVersionDTO {
    private Long id;
    private Long applicationId;
    private String versionName;
    private String description;
    private Timestamp startTime;
    private String customFields;
    private Timestamp endTime;
    private ApplicationDTO application;
    private Long UpdatedById;
    private Long CreatedById;
    private Timestamp CreatedDate;
    private Timestamp UpdatedDate;
}
