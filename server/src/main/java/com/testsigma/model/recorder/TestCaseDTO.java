package com.testsigma.model.recorder;

import com.testsigma.dto.TestCaseResultDTO;
import com.testsigma.model.Attachment;
import com.testsigma.model.TestCaseResult;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class TestCaseDTO {

    TestCaseDTO preRequisiteCase;
    private Long id;
    private Long createdById;
    private Timestamp createdDate;
    private Long updatedById;
    private Timestamp updatedDate;
    private Timestamp startTime;
    private Timestamp endTime;
    private Boolean isDataDriven;
    private Boolean isReviewed;
    private Boolean isTestComponent;
    private Long priority;
    private Long requirementId;
    private Long reviewedBy;
    private Timestamp reviewedAt;
    private Timestamp reviewSubmittedAt;
    private Long reviewSubmittedBy;
    private Timestamp draftAt;
    private Long draftBy;
    private Timestamp obsoleteAt;
    private Long obsoleteBy;
    private Timestamp readyAt;
    private Long readyBy;
    private String description;
    private String name;
    private TestCaseStatus status;
    private Long type;
    private Long testDataId;
    private Long userId;
    private Long applicationVersionId;
    private String customFields;
    private Long preRequisite;
    private Long assignee;
    private Boolean isManual;
    private Long copiedFrom;
    private Boolean deleted;
    private Integer testDataIndex;
    private Integer testDataEndIndex;
    private Set<TestCaseResult> results;
    private String priorityName;
    private String statusName;
    private String typeName;
    private String testDataName;
    private String preRequisiteName;
    private String assigneeName;
    private String order;
    private List<Attachment> files;
    private List<String> tags;
    private ApplicationVersionDTO version;
    private String from;
    private String to;
    private String comments;
    private String url;
    private TestDataDTO testData;
    private TestCaseResultDTO lastRun;
    private String baseUrl;
    private Long baseAppId;
    private Long baseAppVersionId;
}

