package com.testsigma.web.request;

import com.testsigma.model.Attachment;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TestCaseRequest {

  private Long id;

  private Timestamp createdDate;

  private Timestamp updatedDate;

  private Timestamp startTime;

  private Timestamp endTime;

  private Boolean isDataDriven;

  private Boolean sendMailNotification;

  private Boolean isStepGroup;

  private Integer priority;

  private Timestamp reviewedAt;

  private Timestamp reviewSubmittedAt;

  private Timestamp draftAt;

  private Timestamp obsoleteAt;

  private Timestamp readyAt;

  private String description;

  private String name;

  private TestCaseStatus status;

  private Integer type;

  private Long testDataId;

  private Long userId;

  private Long workspaceVersionId;

  private Long preRequisite;

  private Long copiedFrom;

  private Boolean deleted;

  private Integer testDataStartIndex;

  private Integer testDataEndIndex;

  private String priorityName;

  private String statusName;

  private String typeName;

  private String testDataName;

  private String preRequisiteName;

  private String assigneeName;

  private String order;

  private List<Attachment> files;

  private List<String> tags;

  private MultipartFile[] mfiles;

}
