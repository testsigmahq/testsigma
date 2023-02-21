package com.testsigma.automator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Data
@ToString
@EqualsAndHashCode
public class TestCaseStepResult implements Serializable {
  private Long id;
  private Long envRunId;
  private Long testCaseId;
  private Long testCaseStepId;
  private Long stepGroupId;
  private Long testCaseResultId;
  private Long parentResultId;
  private ResultConstant result;
  private Integer errorCode;
  private String message;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private String screenshotName;
  @ToString.Exclude
  private String webDriverException;
  private Long parentId;
  private ConditionType conditionType;
  private TestStepType testCaseStepType;
  private Boolean skipExe = false;
  private String skipMessage;
  private Boolean isConditionSuccess = false;
  private String iteration;
  private String testDataProfileName;
  private Integer index;
  private String rootMsg;
  private Integer waitTime;
  private boolean isStepGroupMajorStepFailure = false;
  private ElementEntity element;
  private TestStepPriority priority;
  private Integer retriedCount = 0;
  private StepDetails stepDetails;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private String addonActionLogs;
  private Map<String, ElementPropertiesEntity> ElementDetails = new HashMap<>();
  private Map<String,  TestDataPropertiesEntity> testDataDetails = new LinkedHashMap<>();

  @ToString.Exclude
  private StepResultMetadataEntity metadata = new StepResultMetadataEntity();
  private Map<String, String> outputData = new HashMap<String, String>();
  private List<TestCaseStepResult> stepResults = new ArrayList<TestCaseStepResult>();
  private List<SuggestionEngineResult> suggestionResults = new ArrayList<>();
  private TestPlanRunSettingEntity testPlanRunSettingEntity;
  private Boolean isBreakLoop = false;
  private Boolean isContinueLoop = false;
  private Boolean visualEnabled = false;
}
