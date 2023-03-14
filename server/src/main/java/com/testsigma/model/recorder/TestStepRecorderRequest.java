package com.testsigma.model.recorder;

import com.testsigma.model.*;
import com.testsigma.web.request.ForLoopConditionRequest;
import com.testsigma.web.request.RestStepDetailsRequest;
import lombok.Data;

@Data
public class TestStepRecorderRequest {
    private Long id;
    private String stepDescription;
    private TestStepPriority priority;
    private Integer position;
    private Long preRequisiteStepId;
    private String action;
    private Long testCaseId;
    private Long testComponentId;
    private String customFields;
    //private ResultConstant[] ifConditionExpectedResults;
    private TestStepRecorderDataMap dataMap;
    private String exceptedResult;
    private Integer templateId;
    private TestStepType type;
    private Integer waitTime;
    private TestStepConditionType conditionType;
    private Long parentId;
    private Long copiedFrom;
    private RestStepDetailsRequest restStep;
    private Long testDataId;
    private Long phoneNumberId;
    private Long kibbutzPluginNlpId;
    private KibbutzPluginNLPData kibbutzPluginNlpData;
    private ForLoopConditionRecorderRequest forLoopCondition;
    private Long mailBoxId;
    private Boolean disabled;
    private Boolean ignoreStepResult;
    private String currentImgBase64;
    private String currentPageSource;
    private Boolean visualEnabled = false;
    private Long testDataProfileStepId;
    private Long hostTestCaseStepId;
    public Boolean isStepGroupSpotEdit = false;
    private Long blockId;
    private Boolean isStepRecorder = false;
    private UiIdentifierRequest elementRequest;

}