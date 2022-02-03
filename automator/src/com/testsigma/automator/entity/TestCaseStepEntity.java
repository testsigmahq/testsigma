package com.testsigma.automator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

@Data
@ToString
@EqualsAndHashCode
public class TestCaseStepEntity implements Cloneable {

  public static final String REST_DETAILS_KEY = "rest_details";
  public static final String MASKED_ENITY = "masked_enity";
  public LinkedHashMap<String, TestDataPropertiesEntity> testDataMap;
  public Map<String, ElementPropertiesEntity> elementsMap;
  public Map<String, AttributePropertiesEntity> attributesMap;
  public AddonNaturalTextActionEntity addonNaturalTextActionEntity;
  private Long id;
  private ResultConstant[] ifConditionExpectedResults;
  private Long testCaseId;
  private Long executionId;
  private Long preRequisite;
  private Long parentId;
  private TestStepPriority priority;
  private TestStepType type;
  private Integer waitTime;
  private Long stepGroupId;
  private Integer index;
  private String screenshotPath;
  private Integer naturalTextActionId;
  private ConditionType conditionType;
  private Long position;
  private String element;
  private String parentElementName;
  private String elementName;
  private String locatorValue;
  private String testDataType;
  private String testDataName;
  private String testDataValue;
  private String testDataValuePreSignedURL;
  private String locatorStrategy;
  private Boolean hasPassword = false;
  private String attribute;
  private String iteration;
  private String testDataProfileName;
  private String action;
  private String snippetClass;
  private StepDetails stepDetails;
  private Map<String, KibbutzTestStepTestData> kibbutzTestData;
  private Map<String, KibbutzElementData> kibbutzElements;
  private Map<String, Object> additionalData = new HashMap<>();
  private List<TestCaseStepEntity> testCaseSteps = new ArrayList<TestCaseStepEntity>();
  private Map<String, String> additionalScreenshotPaths = new HashMap<>();
  public List<KibbutzPluginTestDataFunctionEntity> kibbutzPluginTDFEntityList;
  //Used only on automator side
  private Screenshot screenshot;
  private int noOfRetriesOnStepFailure = 1;

  public TestCaseStepEntity clone() throws CloneNotSupportedException {
    TestCaseStepEntity exeStepEntity = (TestCaseStepEntity) super.clone();
    List<TestCaseStepEntity> steps = new ArrayList<TestCaseStepEntity>();
    for (TestCaseStepEntity stepEntity : testCaseSteps) {
      steps.add(stepEntity.clone());
    }
    exeStepEntity.setTestCaseSteps(steps);
    return exeStepEntity;
  }
}
