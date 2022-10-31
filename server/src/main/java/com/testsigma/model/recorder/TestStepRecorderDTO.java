package com.testsigma.model.recorder;

import com.testsigma.dto.RestStepDTO;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepType;
import lombok.Data;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TestStepRecorderDTO implements Cloneable, Serializable {

    private Long id;
    private String stepDescription;
    private TestStepPriority priority;
    private Integer position;
    private Long preRequisiteStepId;
    private String action;
    private Long testCaseId;
    private Long testComponentId;
    private TestStepRecorderDataMap dataMap;
    private String exceptedResult;
    private Integer templateId;
    private TestStepType type;
    private Integer waitTime;
    private TestStepConditionType conditionType;
    private Long parentId;
    private RestStepDTO restStep;
    private Long phoneNumberId;
    private Long kibbutzPluginNlpId;
    //private KibbutzPluginNLPData kibbutzPluginNlpData;
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
    private List<TestStepRecorderDTO> testStepDTOS = new ArrayList<>();
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
    //private List<UiIdentifierOverRiddenMappingDTO> uiIdentifierOverRiddenMappings;
    //private ForLoopOverRiddenMappingDTO forLoopOverRiddenMapping;
    public TestStepRecorderDTO clone() throws CloneNotSupportedException {
        TestStepRecorderDTO entity = (TestStepRecorderDTO) super.clone();
        List<TestStepRecorderDTO> steps = new ArrayList<>();
        for (TestStepRecorderDTO step : testStepDTOS) {
            steps.add(step.clone());
        }
        entity.setTestStepDTOS(steps);
        return entity;
    }

    public Map<String, Object> getDataMapJson() {
        if (dataMap != null) {
            return new JSONObject(dataMap).toMap();
        } else {
            return null;
        }
    }
}

