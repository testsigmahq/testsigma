package com.testsigma.web.request;

import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
@Log4j2
public class TestStepResultRequest {
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
  private TestStepConditionType conditionType;
  private TestStepType testCaseStepType;
  private Boolean skipExe = false;
  private String skipMessage;
  private Boolean isConditionSuccess = false;
  private String iteration;
  private String testDataProfileName;
  private Integer index;
  private String rootMsg;
  private Long groupResultId;
  private Integer waitTime;
  private TestStepPriority priority;
  private StepDetailsRequest stepDetails;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private String addonActionLogs;
  @ToString.Exclude
  private StepResultMetadataRequest metadata = new StepResultMetadataRequest();
  private Map<String, String> outputData = new HashMap<String, String>();
  private List<TestStepResultRequest> stepResults = new ArrayList<TestStepResultRequest>();
  private List<SuggestionEngineResultRequest> suggestionResults = new ArrayList<SuggestionEngineResultRequest>();
  private Map<String, ElementProperties> ElementDetails;
  private Map<String, TestDataPropertiesEntityRequest> testDataDetails;
  private Boolean visualEnabled = false;

}
