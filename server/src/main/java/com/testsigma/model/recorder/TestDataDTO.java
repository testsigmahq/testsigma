package com.testsigma.model.recorder;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TestDataDTO {
    private Long id;
    private String testDataName;
    private String testData;
    private List<TestDataSetDTO> data;
    private Long createdById;
    private Long updatedById;
    private List<String> columns;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private List<String> passwords;
    private Long versionId;
    private Boolean isMigrated;
}

