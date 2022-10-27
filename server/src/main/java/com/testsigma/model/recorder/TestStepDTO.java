package com.testsigma.model.recorder;

import com.testsigma.dto.RestStepDTO;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepType;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestStepDTO implements Cloneable {

    private Long id;
    private String stepDescription;
    private TestStepPriority priority;
    private Integer position;
    private Long preRequisiteStepId;
    private String action;
    private Long testCaseId;
    private Long testComponentId;
    // private TestStepDataMap dataMap;
    private String exceptedResult;
    // private Integer templateId;
    private TestStepType type;
    private Integer waitTime;
    private TestStepConditionType conditionType;
    private Long parentId;
    private RestStepDTO restStep;
    private Long phoneNumberId;
    private Long kibbutzPluginNlpId;
    //--private AddonPluginNLPData kibbutzPluginNlpData;
    private Long mailBoxId;
    private Boolean disabled;
    private Boolean ignoreStepResult;
    private Long importedId;
    private Long testDataId;
    private Integer testDataIndex;
    private String setName;
    private String testDataProfileName;
    private Boolean visualEnabled = false;
    private TestCaseEntityDTO componentTestCaseEntity;
    private Boolean processedAsSubStep = Boolean.FALSE;
    private Long testDataProfileStepId;
    private List<TestStepDTO> testStepDTOS = new ArrayList<>();
    private String screenShotURL;
    private String pageSourceUrl;
    private String pageSource;
    private Integer index;
    private Long blockId;
    private Boolean hasInvalidUiIdentifier;
    private Boolean hasInvalidTestData;
    private List<String> invalidUiIdentifierList = new ArrayList<>();
    private List<String> invalidTestDataList = new ArrayList<>();
    private UiIdentifierDTO uiIdentifierDTO;
    // private List<TestStepDataOverriddenMappingDTO> testStepDataOverRiddenMappings;
    //--private List<ElementOverRiddenMappingDTO> uiIdentifierOverRiddenMappings;
    //--private ForLoopOverRiddenMappingDTO forLoopOverRiddenMapping;
}

