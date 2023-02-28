package com.testsigma.step.processors;

import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.constants.MessageConstants;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.*;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.TestDataSet;
import com.testsigma.model.*;
import com.testsigma.service.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class StepProcessor {
  protected static Integer LOOP_END = -1;
  protected static Integer LOOP_START = -1;
  public static String TESTSIGMA_STORAGE = "testsigma-storage:/";
  protected String testDataName = null;
  private String leftParamValue;
  private String rightParamValue;
  protected Long testPlanId;
  protected List<TestCaseStepEntityDTO> testCaseStepEntityDTOS;
  protected WorkspaceType workspaceType;
  protected Map<String, Element> elementMap;
  protected TestDataSet testDataSet;
  protected TestStepDTO testStepDTO;
  protected Map<String, String> environmentParameters;
  protected TestCaseEntityDTO testCaseEntityDTO;
  protected String environmentParamSetName;
  protected String dataProfile;
  protected TestDataProfileService testDataProfileService;
  protected ElementMapper elementMapper;
  protected NaturalTextActionsService naturalTextActionsService;
  protected StorageService storageService;
  protected RestStepService restStepService;
  protected TestStepMapper testStepMapper;
  protected ProxyAddonService addonService;
  protected TestStepService testStepService;
  protected TestDataProfileMapper testDataProfileMapper;
  protected DefaultDataGeneratorService defaultDataGeneratorService;
  protected Map<Long, Long> dataSetIndex;
  WebApplicationContext webApplicationContext;
  @Getter
  protected ForLoopConditionDTO forLoopConditions;
  protected ForLoopConditionService forLoopConditionsService;
  protected ForLoopConditionsMapper forLoopConditionsMapper;
  protected TestDataSetService testDataSetService;
  protected StorageServiceFactory storageServiceFactory;

  public StepProcessor(WebApplicationContext webApplicationContext, List<TestCaseStepEntityDTO> testCaseStepEntityDTOS,
                       WorkspaceType workspaceType, Map<String, Element> elementMap,
                       TestStepDTO testStepDTO, Long testPlanId, TestDataSet testDataSet,
                       Map<String, String> environmentParameters, TestCaseEntityDTO testCaseEntityDTO, String environmentParamSetName,
                       String dataProfile, Map<Long, Long> dataSetIndex) {
    this.webApplicationContext = webApplicationContext;
    this.testCaseStepEntityDTOS = testCaseStepEntityDTOS;
    this.workspaceType = workspaceType;
    this.elementMap = elementMap;
    this.testStepDTO = testStepDTO;
    this.testPlanId = testPlanId;
    this.testDataSet = testDataSet;
    this.environmentParameters = environmentParameters;
    this.testCaseEntityDTO = testCaseEntityDTO;
    this.environmentParamSetName = environmentParamSetName;
    this.dataProfile = dataProfile;
    this.dataSetIndex = dataSetIndex;
    this.testDataProfileService = (TestDataProfileService) webApplicationContext.getBean("testDataProfileService");
    this.testStepService = (TestStepService) webApplicationContext.getBean("testStepService");
    this.elementMapper = (ElementMapper) webApplicationContext.getBean("elementMapperImpl");
    this.testStepMapper = (TestStepMapper) webApplicationContext.getBean("testStepMapperImpl");
    this.naturalTextActionsService = (NaturalTextActionsService) webApplicationContext.getBean("naturalTextActionsService");
    this.storageService = webApplicationContext.getBean(StorageServiceFactory.class).getStorageService();
    this.restStepService = (RestStepService) webApplicationContext.getBean("restStepService");
    this.defaultDataGeneratorService = (DefaultDataGeneratorService) webApplicationContext.getBean("defaultDataGeneratorService");
    this.testStepMapper = (TestStepMapper) webApplicationContext.getBean("testStepMapperImpl");
    this.testDataProfileMapper = (TestDataProfileMapper) webApplicationContext.getBean("testDataProfileMapperImpl");
    this.addonService = (ProxyAddonService) webApplicationContext.getBean("proxyAddonService");
    this.forLoopConditionsService = (ForLoopConditionService) webApplicationContext.getBean("forLoopConditionService");
    this.forLoopConditionsMapper = (ForLoopConditionsMapper) webApplicationContext.getBean(
            "forLoopConditionsMapperImpl");
    this.testDataSetService = (TestDataSetService) webApplicationContext.getBean("testDataSetService");
    this.storageServiceFactory = (StorageServiceFactory) webApplicationContext.getBean("storageServiceFactory");
  }

  protected void processDefault(TestCaseStepEntityDTO exeTestStepEntity) throws TestsigmaException {
    exeTestStepEntity.setId(testStepDTO.getId());
    exeTestStepEntity.setType(testStepDTO.getType());
    exeTestStepEntity.setTestCaseId(testStepDTO.getTestCaseId());
    exeTestStepEntity.setAction(testStepDTO.getAction());
    exeTestStepEntity.setTestPlanId(testPlanId);
    exeTestStepEntity.setPriority(testStepDTO.getPriority());
    exeTestStepEntity.setPreRequisite(testStepDTO.getPreRequisiteStepId());
    exeTestStepEntity.setPosition(testStepDTO.getPosition());
    exeTestStepEntity.setVisualEnabled(testStepDTO.getVisualEnabled());
    exeTestStepEntity.setIfConditionExpectedResults(testStepDTO.getIfConditionExpectedResults());
    exeTestStepEntity.setAdditionalData(testStepDTO.getDataMapJson());
    exeTestStepEntity.setAddonTestData(testStepDTO.getAddonTestData());
    exeTestStepEntity.setTestDataProfileStepId(testStepDTO.getTestDataProfileStepId());
    populateStepDetails(testStepDTO, exeTestStepEntity);
  }

  public TestCaseStepEntityDTO processStep() throws TestsigmaException {
    TestCaseStepEntityDTO exeTestStepEntity = new TestCaseStepEntityDTO();
    exeTestStepEntity.setId(testStepDTO.getId());
    exeTestStepEntity.setType(testStepDTO.getType());
    exeTestStepEntity.setNaturalTextActionId(testStepDTO.getNaturalTextActionId());
    exeTestStepEntity.setTestCaseId(testStepDTO.getTestCaseId());
    exeTestStepEntity.setTestDataName(testStepDTO.getTestDataProfileName());
    exeTestStepEntity.setAction(testStepDTO.getAction());
    exeTestStepEntity.setTestPlanId(testPlanId);
    exeTestStepEntity.setPriority(testStepDTO.getPriority());
    exeTestStepEntity.setPreRequisite(testStepDTO.getPreRequisiteStepId());
    exeTestStepEntity.setPosition(testStepDTO.getPosition());
    exeTestStepEntity.setWaitTime(testStepDTO.getWaitTime() == null ? 0 : testStepDTO.getWaitTime());
    exeTestStepEntity.setIfConditionExpectedResults(testStepDTO.getIfConditionExpectedResults());
    exeTestStepEntity.setAdditionalData(testStepDTO.getDataMapJson());
    exeTestStepEntity.setTestDataProfileStepId(testStepDTO.getTestDataProfileStepId());
    exeTestStepEntity.setTestDataIndex(testCaseEntityDTO.getTestDataIndex());
    exeTestStepEntity.setTestDataProfileName(testCaseEntityDTO.getTestDataProfileName());
    exeTestStepEntity.setVisualEnabled(testStepDTO.getVisualEnabled());
    exeTestStepEntity.setParentId(testStepDTO.getParentId());
    exeTestStepEntity.setIndex(testStepDTO.getIndex());
    exeTestStepEntity.setMaxIterations(testStepDTO.getMaxIterations());
    exeTestStepEntity.setTestDataIndex(testCaseEntityDTO.getTestDataIndex());
    exeTestStepEntity.setTestDataProfileName(testCaseEntityDTO.getTestDataProfileName());
    populateStepDetails(testStepDTO, exeTestStepEntity);
    //attachTestDataProfileStepId(testCaseStepEntityDTOS);


    if ((testStepDTO.getType() != null &&
      (testStepDTO.getType() == com.testsigma.model.TestStepType.STEP_GROUP)
      || (testStepDTO.getType() == com.testsigma.model.TestStepType.FOR_LOOP))) {
      return exeTestStepEntity;
    }

    if (testStepDTO.getNaturalTextActionId() != null && testStepDTO.getNaturalTextActionId() > 0) {
      NaturalTextActions naturalTextActions = naturalTextActionsService.findById(testStepDTO.getNaturalTextActionId().longValue());
      exeTestStepEntity.setSnippetClass(naturalTextActions.getSnippetClass());
    } else if (testStepDTO.getAddonActionId() != null) {
      exeTestStepEntity.setSnippetEnabled(Boolean.FALSE);
      exeTestStepEntity.setAddonNaturalTextActionEntity(addonService.fetchPluginEntity(testStepDTO.getAddonActionId()));
    }

    if (testDataSet != null)
      exeTestStepEntity.setSetName(testDataSet.getName());
    exeTestStepEntity.setTestDataId(testCaseEntityDTO.getTestDataId());
    exeTestStepEntity.setIndex(testStepDTO.getIndex());
    setElementMap(exeTestStepEntity);
    setTestDataMap(exeTestStepEntity);
    setAttributesMap(exeTestStepEntity);
    exeTestStepEntity.getStepDetails().setTestDataName(exeTestStepEntity.getTestDataName());
    exeTestStepEntity.getStepDetails().setTestDataValue(exeTestStepEntity.getTestDataValue());
    setAddonPluginStepDetails(exeTestStepEntity);
    return exeTestStepEntity;
  }

  private void attachTestDataProfileStepId(List<TestCaseStepEntityDTO> testCaseStepEntityDTOS) {
    for (TestCaseStepEntityDTO testStepEntity : testCaseStepEntityDTOS){
      if (testStepEntity.getTestDataProfileStepId()!=null){
        Optional<TestCaseStepEntityDTO> TDPStepEntity = testCaseStepEntityDTOS.stream().filter(step -> Objects.equals(step.getStepDetails().getDataMap().getForLoop().getTestDataId(), testStepEntity.getTestDataProfileStepId())).findFirst();
        testStepEntity.setTestDataProfileStepId(TDPStepEntity.get().getId());
      }
    }
  }

  public void setElementMap(TestCaseStepEntityDTO exeTestStepEntity) throws TestsigmaException {
    Map<String, ElementPropertiesDTO> elementsMap = new HashMap<>();
    if (testStepDTO.getAddonActionId() != null) {
      Map<String, AddonElementData> elements = testStepDTO.getAddonElements();
      for (Map.Entry<String, AddonElementData> entry : elements.entrySet()) {
        AddonElementData addonElementData = entry.getValue();
        String elementName = addonElementData.getName();
        ElementPropertiesDTO elementPropertiesDTO = getElementEntityDTO(elementName);
        elementsMap.put(entry.getKey(), elementPropertiesDTO);
      }
    } else {
      String elementName = testStepDTO.getElement();
      String fromElementName = testStepDTO.getFromElement();
      String toElementName = testStepDTO.getToElement();
      if (!org.apache.commons.lang3.StringUtils.isEmpty(elementName)) {
        ElementPropertiesDTO elementPropertiesDTO = getElementEntityDTO(elementName);
        elementsMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_ELEMENT, elementPropertiesDTO);
      }
      if (!org.apache.commons.lang3.StringUtils.isEmpty(fromElementName)) {
        ElementPropertiesDTO elementPropertiesDTO = getElementEntityDTO(fromElementName);
        elementsMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT, elementPropertiesDTO);
      }
      if (!org.apache.commons.lang3.StringUtils.isEmpty(toElementName)) {
        ElementPropertiesDTO elementPropertiesDTO = getElementEntityDTO(toElementName);
        elementsMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TO_ELEMENT, elementPropertiesDTO);
      }

    }
    exeTestStepEntity.setElementsMap(elementsMap);
  }

  public void setTestDataMap(TestCaseStepEntityDTO exeTestStepEntity) throws TestsigmaException {
    LinkedHashMap<String, com.testsigma.automator.entity.TestDataPropertiesEntity> testDatasMap = new LinkedHashMap<>();
    if (testStepDTO.getAddonActionId() != null || testStepDTO.getAddonTestData() != null) {
      Map<String, AddonTestStepTestData> testDataMap = testStepDTO.getAddonTestData();
      for (Map.Entry<String, AddonTestStepTestData> entry : testDataMap.entrySet()) {
        AddonTestStepTestData addonTestStepTestData = entry.getValue();
        String testDataName = entry.getKey();
        String testDataValue = addonTestStepTestData.getValue();
        String testDataType = addonTestStepTestData.getType().getDispName();
        com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity = getTestDataEntityDTO(testDataName,
          testDataValue, testDataType, addonTestStepTestData,exeTestStepEntity);
        if (com.testsigma.model.TestDataType.getTypeFromName(testDataType) == com.testsigma.model.TestDataType.raw) {
          testDataPropertiesEntity.setTestDataValue(addonTestStepTestData.getValue());
        }
        testDatasMap.put(entry.getKey(), testDataPropertiesEntity);
      }
    } else if(testStepDTO.getDataMap() != null && testStepDTO.getDataMap().getTestData() != null){
      for(String key : testStepDTO.getDataMap().getTestData().keySet()) {
        String testDataName = key;
        String testDataValue = testStepDTO.getDataMap().getTestData().get(key).getValue();
        String testDataType = testStepDTO.getDataMap().getTestData().get(key).getType();

        if (!org.apache.commons.lang3.StringUtils.isEmpty(testDataName)) {
          com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity = getTestDataEntityDTO(testDataName,
                  testDataValue, testDataType, null, exeTestStepEntity);
          if (TestDataType.getTypeFromName(testDataType) == TestDataType.raw) {
            testDataPropertiesEntity.setTestDataValue(testDataValue);
          }

          testDatasMap.put(testDataName, testDataPropertiesEntity);
        }
      }
    }
    exeTestStepEntity.setTestDataMap(testDatasMap);
  }

  public void setAttributesMap(TestCaseStepEntityDTO exeTestStepEntity) {
    Map<String, AttributePropertiesEntityDTO> attributesMap = new HashMap<>();
    //Custom Action (Or Addon) doesn't have the concept of attributes. They are treated as test-data itself
    //Even normal Action shouldn't have them but since it was supported earlier so we are keeping it for now
    //And should be migrated as normal test data later.
    if (testStepDTO.getAddonActionId() == null) {
      String attributeName = testStepDTO.getAttribute();
      if (!org.apache.commons.lang3.StringUtils.isEmpty(attributeName)) {
        AttributePropertiesEntityDTO attributePropertiesEntityDTO = new AttributePropertiesEntityDTO();
        attributePropertiesEntityDTO.setAttributeName(attributeName);
        attributesMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_ATTRIBUTE, attributePropertiesEntityDTO);
      }
    }
    exeTestStepEntity.setAttributesMap(attributesMap);
  }

  private ElementPropertiesDTO getElementEntityDTO(String elementName) throws TestsigmaException {
    Element element = elementMap.get(elementName.toLowerCase());
    ElementDTO elementDTO = elementMapper.map(element);
    if (element == null) {
      throw new TestsigmaException(ExceptionErrorCodes.ELEMENT_NOT_FOUND,
        MessageConstants.getMessage(MessageConstants.ELEMENT_WITH_THE_NAME_IS_NOT_AVAILABLE, elementName));
    }
    String locatorValue = updateElement(element, testDataSet, environmentParameters);
    ElementPropertiesDTO elementPropertiesDTO = new ElementPropertiesDTO();
    elementPropertiesDTO.setElementName(elementName);
    elementPropertiesDTO.setLocatorValue(locatorValue);
    elementPropertiesDTO.setLocatorStrategyName(element.getLocatorType().toString());
    elementPropertiesDTO.setFindByType(FindByType.getType(element.getLocatorType()));
    elementPropertiesDTO.setElementEntity(elementDTO);
    return elementPropertiesDTO;
  }

    private com.testsigma.automator.entity.TestDataPropertiesEntity getTestDataEntityDTO(String testDataName, String testDataValue,
                                                                                         String testDataType, AddonTestStepTestData
                                                                                                 addonTestStepTestData, TestCaseStepEntityDTO testCaseStepEntityDTO)
    throws TestsigmaException {
    com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity = new com.testsigma.automator.entity.TestDataPropertiesEntity();
    testDataPropertiesEntity.setTestDataType(testDataType);

    switch (com.testsigma.model.TestDataType.getTypeFromName(testDataType)) {
      case global:
        if ((environmentParameters == null)) {
          throw new TestsigmaException(ExceptionErrorCodes.ENVIRONMENT_PARAMETERS_NOT_CONFIGURED,
            MessageConstants.getMessage(MessageConstants.MSG_UNKNOWN_ENVIRONMENT_DATA_SET));
        } else if (environmentParameters.get(testDataValue) == null) {
          throw new TestsigmaException(ExceptionErrorCodes.ENVIRONMENT_PARAMETER_NOT_FOUND,
            MessageConstants.getMessage(MessageConstants.MSG_UNKNOWN_ENVIRONMENT_PARAMETER_IN_TEST_STEP, testDataValue,
              testCaseEntityDTO.getTestCaseName(), environmentParamSetName));
        }
        testDataValue = environmentParameters.get(testDataValue);
        break;
      case parameter:
        testDataName = testDataValue;
        testDataValue = populateTestDataParameter(testDataSet, testCaseStepEntityDTO, testDataValue, testDataPropertiesEntity);
        break;
      case random:
      case runtime:
        break;
      case function:
        populateDefaultDataGeneratorsEntity(testDataPropertiesEntity, addonTestStepTestData,testCaseStepEntityDTO);
        break;
      default:
    }
    testDataPropertiesEntity.setTestDataName(testDataName);
    testDataPropertiesEntity.setTestDataValue(testDataValue);
    return testDataPropertiesEntity;
  }


  public TestDataSet getTestDataIdFromStep(TestCaseStepEntityDTO step) throws ResourceNotFoundException {
    try {
      TestStep testStep = testStepService.find(step.getTestDataProfileStepId());
      TestData testData = testDataProfileService.find(testStep.getDataMap().getForLoop().getTestDataId());
      return testData.getTempTestData().get(dataSetIndex.get(testStep.getId()).intValue());
    } catch (Exception e) {
      TestStep parentStep = step.getParentId() != null ? testStepService.find(step.getParentId()) : null;
      if (Objects.equals(step.getTestDataProfileStepId(), this.testCaseEntityDTO.getTestDataId())) {
        TestData testData = testDataProfileService.find(step.getTestDataProfileStepId());
        return testData.getTempTestData().get(this.testCaseEntityDTO.getTestDataIndex());
      } else if (parentStep != null) {
        TestData testData = testDataProfileService.find(parentStep.getDataMap().getForLoop().getTestDataId());
        return testData.getTempTestData().get(dataSetIndex.get(parentStep.getId()).intValue());
      } else
        return null;
    }
  }

  public String populateTestDataParameter(TestDataSet testDataSet, TestCaseStepEntityDTO exeTestStepEntity,
                                          String testDataValue, com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity) throws ResourceNotFoundException {
    boolean isParentStepExists = false;
    try{
      if(exeTestStepEntity.getTestDataProfileStepId() != null){
        testDataSet = getTestDataIdFromStep( exeTestStepEntity);
        if (exeTestStepEntity.getParentId() != null && testDataSet != null && !(Objects.equals(exeTestStepEntity.getTestDataProfileStepId(), this.testCaseEntityDTO.getTestDataId())))
          isParentStepExists = true;
      }
    }catch(Exception exception){
      log.error(exception,exception);
    }

    if (((testDataSet == null) || (testDataSet.getData() == null))) {
      if(!isParentStepExists)
        throw new ResourceNotFoundException(com.testsigma.constants.MessageConstants.getMessage(MessageConstants.MSG_UNKNOWN_TEST_DATA_SET));
      else{
        exeTestStepEntity.setFailureMessage(String.format(MessageConstants.TEST_DATA_NOT_FOUND, testDataValue));
      }
    }

    String originalTestDataValue = testDataValue;
    testDataValue = testDataSet.getData().has(testDataValue) ? (String) testDataSet.getData().get(testDataValue) : null;

    if (testDataValue == null) {
      if(!isParentStepExists){
        throw new ResourceNotFoundException(MessageConstants.getMessage(MessageConstants.MSG_UNKNOWN_TEST_DATA_PARAMETER_IN_TEST_STEP ,
                originalTestDataValue , testCaseEntityDTO.getTestCaseName() ,  dataProfile));
      }else{
        exeTestStepEntity.setFailureMessage(String.format(MessageConstants.TEST_DATA_NOT_FOUND, testDataValue));
      }
    }
    return testDataValue;
  }

  public void populateStepDetails(TestStepDTO testStepDTO, TestCaseStepEntityDTO testCaseStepEntityDTO) {
    StepDetailsDTO stepDetails = new StepDetailsDTO();
    stepDetails.setNaturalTextActionId(testStepDTO.getNaturalTextActionId());
    stepDetails.setAction(testStepDTO.getAction());
    stepDetails.setPriority(Optional.ofNullable(testStepDTO.getPriority()).orElse(null));
    stepDetails.setPreRequisiteStepId(testStepDTO.getPreRequisiteStepId());
    stepDetails.setConditionType(testStepDTO.getConditionType());
    stepDetails.setParentId(testStepDTO.getParentId());
    stepDetails.setType(Optional.ofNullable(testStepDTO.getType()).orElse(null));
    stepDetails.setStepGroupId(testStepDTO.getStepGroupId());
    stepDetails.setAction(testStepDTO.getAction());
    stepDetails.setPosition(testStepDTO.getPosition());
    stepDetails.setTestDataName(testCaseStepEntityDTO.getTestDataName());
    stepDetails.setTestDataValue(testCaseStepEntityDTO.getTestDataValue());
    stepDetails.setDataMap(testStepMapper.mapDataMap(testStepDTO.getDataMapBean()));
    stepDetails.setIgnoreStepResult(testStepDTO.getIgnoreStepResult());
    stepDetails.setMaxIterations(testStepDTO.getMaxIterations());
    testCaseStepEntityDTO.setStepDetails(stepDetails);
  }

  private void setAddonPluginStepDetails(TestCaseStepEntityDTO exeTestStepEntity) {
    if (testStepDTO.getAddonActionId() != null) {
      exeTestStepEntity.setAddonTestData(testStepDTO.getAddonTestData());
      exeTestStepEntity.setAddonElements(testStepDTO.getAddonElements());
    }
  }

  public void loadLoop(TestStepDTO stepDTOEntity, List<TestStepDTO> stepDTOEntities) {
    List<TestStepDTO> loopSubSteps = new ArrayList<>();
    List<Long> childConditionalStepIds = new ArrayList<>();
    for (TestStepDTO childTestStepDTO : stepDTOEntities) {
      if ((childTestStepDTO.getParentId() != null && childTestStepDTO.getParentId() > 0)
              && ((childTestStepDTO.getParentId().equals(stepDTOEntity.getId()))
              || (childConditionalStepIds.contains(childTestStepDTO.getParentId())))) {
        if (childTestStepDTO.getType() != null &&
                (TestStepType.FOR_LOOP.equals(childTestStepDTO.getType())
                        || TestStepConditionType.LOOP_WHILE.equals(childTestStepDTO.getConditionType()))) {
          loadLoop(childTestStepDTO, stepDTOEntities);
        } else {
          childConditionalStepIds.add(childTestStepDTO.getId());
        }
        childTestStepDTO.setProcessedAsSubStep(Boolean.TRUE);
        loopSubSteps.add(childTestStepDTO);
      }
    }
    stepDTOEntity.setTestStepDTOS(loopSubSteps);
  }

  private void populateDefaultDataGeneratorsEntity(com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity,
                                                   AddonTestStepTestData addonTestStepTestData, TestCaseStepEntityDTO exeTestStepEntity)
    throws TestsigmaException {
    DefaultDataGeneratorsEntity defaultDataGeneratorsEntity = new DefaultDataGeneratorsEntity();
    try {
      if (testStepDTO.getAddonActionId() != null) {
        populateTestDataFunctionDetailsFromId(defaultDataGeneratorsEntity, addonTestStepTestData, exeTestStepEntity);
      } else {
        populateTestDataFunctionDetailsFromMap(defaultDataGeneratorsEntity, exeTestStepEntity);
      }
      testDataPropertiesEntity.setDefaultDataGeneratorsEntity(defaultDataGeneratorsEntity);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }
  private void populateTestDataFunctionDetailsFromId(DefaultDataGeneratorsEntity testDataFunctionEntity,
                                                     AddonTestStepTestData addonTestStepTestData, TestCaseStepEntityDTO exeTestStepEntity) throws ResourceNotFoundException {
    Map<String, String> arguments = addonTestStepTestData.getTestDataFunctionArguments();
    testDataFunctionEntity.setArguments(arguments);
    if (addonTestStepTestData.getIsAddonFn()) {
      AddonPluginTestDataFunctionEntityDTO tdfEntityDTO = addonService.fetchPluginTestDataFunctionEntities(addonTestStepTestData.getTestDataFunctionId());
      ArrayList<AddonPluginTestDataFunctionEntityDTO> tdfEntityDTOList = new ArrayList<AddonPluginTestDataFunctionEntityDTO>();
      tdfEntityDTOList.add(tdfEntityDTO);
      if (exeTestStepEntity.getAddonPluginTDFEntityList() == null) {
        exeTestStepEntity.setAddonPluginTDFEntityList(tdfEntityDTOList);
      } else {
        exeTestStepEntity.getAddonPluginTDFEntityList().addAll(tdfEntityDTOList);
      }
      testDataFunctionEntity.setIsAddonFn(addonTestStepTestData.getIsAddonFn());
    } else {
      DefaultDataGenerator customFunction = defaultDataGeneratorService.find(addonTestStepTestData.getTestDataFunctionId());
      DefaultDataGeneratorFile customFunctionFile = defaultDataGeneratorService.findFileById(customFunction.getFileId());
      testDataFunctionEntity.setClassName(customFunctionFile.getClassName());
      testDataFunctionEntity.setFunctionName(customFunction.getFunctionName());
      Map<String, String> functionArguments = new ObjectMapperService().parseJson(
        customFunction.getArguments().get("arg_types").toString(), HashMap.class);
      testDataFunctionEntity.setArgumentTypes(functionArguments);
      testDataFunctionEntity.setClassPackage(customFunctionFile.getClassPackage());
    }
    testDataFunctionEntity.setId(addonTestStepTestData.getTestDataFunctionId());
  }

  private void populateTestDataFunctionDetailsFromMap(DefaultDataGeneratorsEntity defaultDataGeneratorsEntity, TestCaseStepEntityDTO exeTestStepEntity)
    throws TestsigmaException {
    TestStepDataMap testStepDataMap = testStepDTO.getDataMapBean();
    for(String key : testStepDataMap.getTestData().keySet()) {
      TestStepNlpData testStepNlpData = testStepDataMap.getTestData().get(key);
      if (testStepNlpData != null) {
        if (testStepNlpData.getAddonTDF() != null && testStepNlpData.getAddonTDF().getValue() != null) {
          defaultDataGeneratorsEntity.setArguments(testStepNlpData.getAddonTDF().getTestDataFunctionArguments());
          defaultDataGeneratorsEntity.setIsAddonFn(true);
          AddonPluginTestDataFunctionEntityDTO tdfEntityDTO = addonService.fetchPluginTestDataFunctionEntities(testStepNlpData.getAddonTDF().getTestDataFunctionId());
          ArrayList<AddonPluginTestDataFunctionEntityDTO> tdfEntityDTOList = new ArrayList<AddonPluginTestDataFunctionEntityDTO>();
          tdfEntityDTOList.add(tdfEntityDTO);
          exeTestStepEntity.setAddonPluginTDFEntityList(tdfEntityDTOList);
          return;
        }
      }
    }
    if (testStepDTO.getTestDataFunctionId() == null) {
      throw new TestsigmaException(ExceptionErrorCodes.TEST_DATA_NOT_FOUND_TEST_STEP,
        MessageConstants.getMessage(MessageConstants.INVALID_TEST_DATA));
    }
    DefaultDataGenerator defaultDataGenerator = defaultDataGeneratorService.find(testStepDTO.getTestDataFunctionId());
    defaultDataGeneratorsEntity.setClassName(defaultDataGenerator.getFile().getClassName());
    defaultDataGeneratorsEntity.setFunctionName(defaultDataGenerator.getFunctionName());
    Map<String, String> args = (HashMap) defaultDataGenerator.getArguments();
    defaultDataGeneratorsEntity.setArguments(args);
    Map<String, String> argsTypes = (HashMap) defaultDataGenerator.getArguments().get("arg_types");
    defaultDataGeneratorsEntity.setArgumentTypes(argsTypes);
    defaultDataGeneratorsEntity.setClassPackage(defaultDataGenerator.getFile().getClassPackage());
  }


  public String updateElement(Element element, TestDataSet testData, Map<String, String> environmentParams) {
    String locatorValue = element.getLocatorValue();
    try {

      if (element.getIsDynamic()) {
      ElementMetaData metaData = element.getMetadata();
      if (metaData.getTestData() != null) {
        //JsonObject to Map Casting failed
        Map<String, Object> rawDataMap = metaData.getTestData().toMap();
        Map<String, String> stringTypeDataMap = new HashMap();
        for (Map.Entry<String, Object> entry : rawDataMap.entrySet()) {
          if (entry.getValue() instanceof String) {
            stringTypeDataMap.put(entry.getKey(), (String) entry.getValue());
          }
        }
        Map<String, String> dataMap = stringTypeDataMap;
        if (dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_TYPE).equals(TestDataType.parameter.name())) {
          //TODO: Handle null and exception cases..
          if (!(testData == null || testData.getData() == null || org.apache.commons.lang3.StringUtils.isEmpty(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA)) ||
            org.apache.commons.lang3.StringUtils.isEmpty(testData.getData().optString(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA))))) {
            String data = testData.getData().getString(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA));
            locatorValue =
              element.getLocatorValue().replaceAll(NaturalTextActionConstants.TEST_DATA_PARAMETER_PREFIX + "\\|" + Pattern.quote(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA)) + "\\|",
                Matcher.quoteReplacement(data));
          }

        } else if (dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_TYPE).equals(TestDataType.global.name())) {
          if (environmentParams != null && StringUtils.isNotEmpty(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA))) {
            String data = environmentParams.get(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA));
            if (data != null) {
              locatorValue =
                element.getLocatorValue().replaceAll(NaturalTextActionConstants.TEST_DATA_ENVIRONMENT_PARAM_PREFIX + "\\|" + Pattern.quote(dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA)) + "\\|",
                  Matcher.quoteReplacement(data));
            }else {
              String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
                MessageConstants.MSG_UNKNOWN_ENVIRONMENT_PARAMETER_IN_ELEMENT, dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA), this.testCaseEntityDTO.getTestCaseName(), element.getName());
                throw new TestsigmaException(ExceptionErrorCodes.ENVIRONMENT_PARAMETER_NOT_FOUND, errorMessage);
            }
          }
          //TODO: Handle null and exception cases..
        } else if (dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_TYPE).equals(TestDataType.runtime.name())) {
          //TODO: Handle null and exception cases..
        } else if (dataMap.get(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_TYPE).equals(TestDataType.function.name())) {
          //TODO: Handle null and exception cases..s
        }
      }
    }
    } catch (TestsigmaException e) {
      log.info(e.getMessage(),e);
    }
    return locatorValue;
  }

  protected Set<TestDataSet> getIterations() throws TestsigmaException {
    log.info("Processing for loop step - " + testStepDTO.getId());
    List<TestDataSet> testDataList = new ArrayList<>();
    Pattern COMMA = Pattern.compile(",");
    Long stepId = testStepDTO.getId();

    if (!testCaseEntityDTO.getIsStepGroup() || forLoopConditions == null) {
      forLoopConditions = forLoopConditionsMapper.map(forLoopConditionsService.findAllByTestCaseStepId(stepId).get());
    }

    Long testDataSetId = dataSetIndex.containsKey(testStepDTO.getId()) ? dataSetIndex.get(testStepDTO.getId())
            : testCaseEntityDTO.getTestDataId();

    if (testDataSetId != null) {
      testDataSet = testDataSetService.find(testDataSetId);
    }
    TestData testData = testDataProfileService.find(forLoopConditions.getTestDataProfileId());
    testDataName = testData.getTestDataName();
    List<TestDataSet> originalDataBank = this.testDataSetService.findByProfileId(testData.getId());
    List<TestDataSet> dataBank = originalDataBank.stream().map(dataSet -> testDataProfileMapper.copySet(dataSet)).collect(Collectors.toList());
    IterationType iterationType = forLoopConditions.getIterationType();

    String leftDataParam = getTestDataValue(forLoopConditions.getLeftParamType(), leftParamValue,
            forLoopConditions.getLeftParamValue(), testData);
    String rightDataParam = getTestDataValue(forLoopConditions.getRightParamType(), rightParamValue,
            forLoopConditions.getRightParamValue(), testData);
    log.debug("LeftDataParam:" + leftDataParam);
    log.debug("rightDataParam:" + rightDataParam);
    log.debug("operator:" + forLoopConditions.getOperator());

    Operator operator = forLoopConditions.getOperator();

    if (iterationType == IterationType.INDEX) {
      if (operator == null)
        operator = Operator.EQUALS;
      Integer start = Integer.parseInt(leftDataParam);
      if (start == -1) {
        start = 1;
      }
      Integer end = Integer.parseInt(rightDataParam);
      if (end == -1) {
        end = dataBank.size();
      }
      start = start - 1;
      end = end - 1;
      if (start < 0) {
        throw new TestsigmaException(String.format(MessageConstants.MSG_INVALID_START_INDEX, start + 1));
      }

      if (end + 1 > dataBank.size()) {
        throw new TestsigmaException(String.format(MessageConstants.MSG_END_INDEX, dataBank.size(), end + 1));
      }

      if (start > end) {
        throw new TestsigmaException(String.format(MessageConstants.MSG_INVALID_START_INDEX_GREATER, start + 1, end + 1));
      }

      testDataList = dataBank.subList(start, end + 1);
    } else if (iterationType == IterationType.PARAMETER_VALUE) {
      String parameterName = leftDataParam;
      List<String> rightParams = (rightDataParam != null) ? COMMA.splitAsStream(rightDataParam).collect(Collectors.toList()) : null;
      testDataList = filterParameterIterations(dataBank, rightParams, operator, parameterName);
    } else if (iterationType == IterationType.SET_NAME && operator != null) {
      List<String> rightParams = COMMA.splitAsStream(rightDataParam).collect(Collectors.toList());
      testDataList = filterIterations(dataBank, rightParams, operator);
    } else if (iterationType == IterationType.SET_NAME) {
      testDataList = filterIterations(dataBank, leftDataParam, rightDataParam);
    }

    return new HashSet<>(testDataList);
  }

  private List<TestDataSet> filterIterations(List<TestDataSet> allList, String startSetName, String endSetName) throws TestsigmaException {
    List<TestDataSet> testDataSetList = new ArrayList<>();

    Optional<TestDataSet> startSet = Objects.equals(startSetName, "-1") ? Optional.of(allList.get(0)) :
            allList.stream().filter(dataSet -> dataSet.getName().equals(startSetName)).findFirst();

    Optional<TestDataSet> endSet = Objects.equals(endSetName, "-1") ? Optional.of(allList.get(allList.size() - 1)) :
            allList.stream().filter(dataSet -> dataSet.getName().equals(endSetName)).findFirst();

    validateSetNamesBoundaries(startSet, endSet, Objects.equals(startSetName, "-1") ? "start" :
            startSetName, Objects.equals(endSetName, "-1") ? "end" : endSetName);
    if (startSet.isPresent() && endSet.isPresent()) {
      testDataSetList = allList.subList(startSet.get().getPosition().intValue(), endSet.get().getPosition().intValue() + 1);
    }
    return testDataSetList;
  }

  private void validateSetNamesBoundaries(Optional<TestDataSet> startSet, Optional<TestDataSet> endSet, String startSetName, String endSetName) throws TestsigmaException {
    if (startSet.isEmpty())
      throw new TestsigmaException(String.format(MessageConstants.MSG_START_SET_NAME_MISSING, startSetName));
    if (endSet.isEmpty())
      throw new TestsigmaException(String.format(MessageConstants.MSG_END_SET_NAME_MISSING, endSetName));

    if (startSet.get().getPosition() > endSet.get().getPosition()) {
      throw new TestsigmaException(String.format(MessageConstants.MSG_START_SET_NAME_GREATER_POSITION_THAN_END_SET_NAME, startSetName, startSet.get().getPosition(), endSetName, endSet.get().getPosition()));
    }
  }

  private List<TestDataSet> filterIterations(List<TestDataSet> allList, List<String> filterNames,
                                             Operator operator) {
    List<TestDataSet> testDataSetList = new ArrayList<>();
    switch (operator) {
      case EQUALS:
      case IN:
        List<String> filterNameList = filterNames.stream().map(filterName -> filterName.toLowerCase()).collect(Collectors.toList());
        testDataSetList = allList.stream().filter(dataSet -> filterNameList.contains(dataSet.getName().toLowerCase())).collect(
                Collectors.toList());
        break;
      case CONTAINS:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getName().toLowerCase().contains(filterName.toLowerCase()))).collect(Collectors.toList());
        break;
      case ENDS_WITH:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getName().toLowerCase().endsWith(filterName.toLowerCase()))).collect(Collectors.toList());
        break;
      case STARTS_WITH:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getName().toLowerCase().startsWith(filterName.toLowerCase()))).collect(Collectors.toList());
        break;

    }
    return testDataSetList;
  }

  private List<TestDataSet> filterParameterIterations(List<TestDataSet> allList, List<String> filterNames, Operator operator,
                                                      String parameter) {
    List<TestDataSet> testDataSetList = new ArrayList<>();
    switch (operator) {
      case EQUALS:
      case IN:
        testDataSetList = allList.stream().filter(dataSet ->
                filterNames.contains(dataSet.getData().getString(parameter))
        ).collect(Collectors.toList());
        break;
      case CONTAINS:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getData().getString(parameter).contains(filterName))).collect(Collectors.toList());
        break;
      case ENDS_WITH:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getData().getString(parameter).endsWith(filterName))).collect(Collectors.toList());
        break;
      case STARTS_WITH:
        testDataSetList = allList.stream().filter(dataSet -> filterNames.stream().anyMatch(
                filterName -> dataSet.getData().getString(parameter).startsWith(filterName))).collect(Collectors.toList());
        break;
      case IS_EMPTY:
        testDataSetList = allList.stream().filter(dataSet -> dataSet.getData().getString(parameter).isEmpty()).collect(Collectors.toList());
        break;
      case IS_NOT_EMPTY:
        testDataSetList = allList.stream().filter(dataSet -> StringUtils.isNotEmpty(dataSet.getData().getString(parameter))).collect(Collectors.toList());
        break;
    }

    return testDataSetList;
  }

  public String getTestDataValue(TestDataType testDataType, String dataParam, String paramValue, TestData testData) throws TestsigmaException {
    String value = null;
    if (testDataType == TestDataType.random || testDataType == TestDataType.runtime ||
            testDataType == TestDataType.function) {
      if (dataParam == null) {
        value = paramValue;
      } else {
        value = dataParam;
      }
    } else if (testDataType == TestDataType.global) {
      if (this.environmentParameters == null) {
        throw new TestsigmaException(String.format(MessageConstants.MSG_ENVIRONMENT_NOT_MAPPED));
      }
      com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity = new TestDataPropertiesEntity();
      TestCaseStepEntityDTO testCaseStepEntityDTO = this.initEntity(testStepDTO);
      TestDataProcessor testDataProcessor = new ParameterTestDataProcessor(testCaseEntityDTO, testCaseStepEntityDTO,
              testCaseStepEntityDTO.getStepGroupParentForLoopStepIdTestDataSetMap(), testDataSet, paramValue, testDataPropertiesEntity, webApplicationContext);
      testDataProcessor.processTestData();
      if (testDataProcessor.getValue() == null) {
        throw new TestsigmaException(String.format(MessageConstants.MSG_ENVIRONMENT_PARAMETER_NOT_FOUND, paramValue, testDataSet.getName()));
      }
      value = getS3Url(testDataProcessor.getValue());

    } else if (testDataType == TestDataType.parameter) {
      TestDataPropertiesEntity testDataPropertiesEntity = new TestDataPropertiesEntity();
      TestCaseStepEntityDTO testCaseStepEntityDTO = this.initEntity(testStepDTO);
      TestDataProcessor testDataProcessor = new ParameterTestDataProcessor(testCaseEntityDTO, testCaseStepEntityDTO,
              testCaseStepEntityDTO.getStepGroupParentForLoopStepIdTestDataSetMap(), testDataSet, paramValue, testDataPropertiesEntity, webApplicationContext);
      testDataProcessor.processTestData();
      testDataPropertiesEntity.setTestDataName(paramValue);
      value = testDataProcessor.getValue();
      if (!testDataProcessor.getIsValueSet() || StringUtils.isEmpty(value)) {
        value = testDataSet.getData().getString(paramValue);
      }
    } else if (testDataType == TestDataType.raw) {
      value = paramValue;
    }
    return value;
  }

  public TestCaseStepEntityDTO initEntity(TestStepDTO testStepDTO) throws ResourceNotFoundException {
    TestCaseStepEntityDTO testStepEntityDTO = new TestCaseStepEntityDTO();
    testStepEntityDTO.setId(testStepDTO.getId());
    testStepEntityDTO.setType(testStepDTO.getType());
    testStepEntityDTO.setNaturalTextActionId(testStepDTO.getNaturalTextActionId());
    testStepEntityDTO.setTestCaseId(testStepDTO.getTestCaseId());
    testStepEntityDTO.setAction(testStepDTO.getAction());
    testStepEntityDTO.setPriority(testStepDTO.getPriority());
    testStepEntityDTO.setPreRequisite(testStepDTO.getPreRequisiteStepId());
    testStepEntityDTO.setPosition(testStepDTO.getPosition());
    testStepEntityDTO.setWaitTime(testStepDTO.getWaitTime() == null ? 0 : testStepDTO.getWaitTime());
    if (testStepDTO.getDataMap() != null) {
      testStepEntityDTO.setIfConditionExpectedResults(testStepDTO.getIfConditionExpectedResults());
    }
    testStepEntityDTO.setAdditionalData(testStepDTO.getDataMapJson());
    testStepEntityDTO.setStepGroupId(testStepDTO.getStepGroupId());
    testStepEntityDTO.setParentId(testStepDTO.getParentId());
    testStepEntityDTO.setConditionType(testStepDTO.getConditionType());
    testStepEntityDTO.setVisualEnabled(testStepDTO.getVisualEnabled());
    testStepEntityDTO.setTestDataIndex(testCaseEntityDTO.getTestDataIndex());
    testStepEntityDTO.setTestDataId(testCaseEntityDTO.getTestDataId());
    testStepEntityDTO.setTestDataProfileName(testCaseEntityDTO.getTestDataProfileName());
    testStepEntityDTO.setStepGroupParentForLoopStepIdTestDataSetMap(this.dataSetIndex);
    testStepEntityDTO.setParentHierarchy(testStepDTO.getParentHierarchy());
    testStepEntityDTO.setForLoopConditionsEntity(forLoopConditions);
    populatedSnippetClassDetails(testStepEntityDTO);
    if (testStepEntityDTO.getTestCaseSteps() == null) {
      testStepEntityDTO.setTestCaseSteps(new ArrayList<>());
    }
    if (testDataSet != null)
      testStepEntityDTO.setSetName(testDataSet.getName());
    testStepEntityDTO.setTestDataId(testCaseEntityDTO.getTestDataId());
    testStepEntityDTO.setIndex(testStepDTO.getIndex());
    testStepEntityDTO.setMaxIterations(testStepDTO.getMaxIterations());
    removeDisabledPrerequisiteStep(testStepEntityDTO);
    return testStepEntityDTO;
  }

  protected void removeDisabledPrerequisiteStep(TestCaseStepEntityDTO testCaseStepEntityDTO) throws ResourceNotFoundException {
    if (testCaseStepEntityDTO.getPreRequisite() != null) {
      TestStep preRequisiteStep = testStepService.find(testCaseStepEntityDTO.getPreRequisite());
      if (Boolean.TRUE.equals(preRequisiteStep.getDisabled())) {
        testCaseStepEntityDTO.setPreRequisite(null);
      }
    }
  }

  protected void populatedSnippetClassDetails(TestCaseStepEntityDTO testCaseStepEntityDTO) throws ResourceNotFoundException {
    if (testStepDTO.getNaturalTextActionId() != null && testStepDTO.getNaturalTextActionId() > 0) {
      NaturalTextActions nlpTemplate = naturalTextActionsService.findById(testStepDTO.getNaturalTextActionId().longValue());
      testCaseStepEntityDTO.setSnippetEnabled(true);
      testCaseStepEntityDTO.setSnippetClass(nlpTemplate.getSnippetClass());
    }
  }

  public String getS3Url(String fileUrl) {
    if (!org.apache.commons.lang3.StringUtils.isEmpty(fileUrl) && fileUrl.contains(TESTSIGMA_STORAGE)) {
      String[] arr = fileUrl.split(",");
      fileUrl = Arrays.stream(arr).map(this::generateS3URL).collect(Collectors.joining(","));
    }
    return fileUrl;
  }

  public String generateS3URL(String fileUrl) {
    if (!org.apache.commons.lang3.StringUtils.isEmpty(fileUrl) && fileUrl.startsWith(TESTSIGMA_STORAGE)) {
      fileUrl = fileUrl.replace(TESTSIGMA_STORAGE, "");;
      String newUrl = fileUrl;
      URL newPreSignedURL = storageServiceFactory.getStorageService()
              .generatePreSignedURL(
                      newUrl,
                      StorageAccessLevel.WRITE,
                      180
              );
      fileUrl = newPreSignedURL.toString();
    }
    return fileUrl;
  }
}
