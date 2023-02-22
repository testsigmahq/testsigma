/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.TestStepDTO;
import com.testsigma.dto.export.TestStepCloudXMLDTO;
import com.testsigma.dto.export.TestStepXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestStepEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.RestStepMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestStepRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestStepSpecificationsBuilder;
import com.testsigma.util.DeprecatedActionMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestStepService extends XMLExportImportService<TestStep> {
    private final TestStepRepository repository;
    private final RestStepService restStepService;
    private final RestStepMapper mapper;
    private final ProxyAddonService addonService;
    private final AddonNaturalTextActionService addonNaturalTextActionService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TestCaseService testCaseService;
    private final TestStepMapper exportTestStepMapper;
    private final TestDataProfileService testDataService;
    private final NaturalTextActionsService naturalTextActionsService;
    private final DefaultDataGeneratorService defaultDataGeneratorService;
    private final ImportAffectedTestCaseXLSExportService affectedTestCaseXLSExportService;
    private final TestStepMapper testStepMapper;

    private final List<ActionTestDataMap> actionTestDataMap = getMapsList();
    private final List<Integer> depreciatedIds = DeprecatedActionMapper.getAllDeprecatedActionIds();


    public List<TestStep> findAllByTestCaseId(Long testCaseId) {
        return this.repository.findAllByTestCaseIdOrderByPositionAsc(testCaseId);
    }

    public List<TestStep> findAllByTestCaseIdAndEnabled(Long testCaseId) {
        List<TestStep> testSteps = repository.findAllByTestCaseIdAndDisabledIsNotOrderByPositionAsc(testCaseId, true);
        List<TestStep> stepGroups = repository.findAllByTestCaseIdAndDisabledIsNotAndStepGroupIdIsNotNullOrderByPositionAsc(testCaseId, true);
        for (TestStep teststep : stepGroups) {
            if (teststep.getStepGroup() != null) {
                List<TestStep> groupsSteps = repository.findAllByTestCaseIdAndDisabledIsNotOrderByPositionAsc(teststep.getStepGroupId(), true);
                teststep.getStepGroup().setTestSteps(new HashSet<>(groupsSteps));
            }
        }
        return testSteps;
    }

    public List<String> findElementNamesByTestCaseIds(List<Long> testCaseIds) {
        List<String> ElementNames = repository.findTestStepsByTestCaseIdIn(testCaseIds);
        return ElementNames.stream().map(x -> StringUtils.strip(x, "\"")).collect(Collectors.toList());
    }

    public List<String> findAddonActionElementsByTestCaseIds(List<Long> testCaseIds) {
        List<String> elementsNames = new ArrayList<>();
        List<TestStep> testSteps = repository.findAllByTestCaseIdInAndAddonActionIdIsNotNull(testCaseIds);
        for (TestStep step : testSteps) {
            Map<String, AddonElementData> addonElementData = step.getAddonElements();
            if (step.getAddonElements() != null) {
                for (AddonElementData elementData : addonElementData.values()) {
                    elementsNames.add(elementData.getName());
                }
            }
        }
        return elementsNames;
    }

    public Page<TestStep> findAll(Specification<TestStep> spec, Pageable pageable) {
        return this.repository.findAll(spec, pageable);
    }

    public void destroy(TestStep testStep, Boolean isRecorderRequest) throws ResourceNotFoundException {
        repository.decrementPosition(testStep.getPosition(), testStep.getTestCaseId());
        if(testStep.getConditionType() == TestStepConditionType.LOOP_WHILE && isRecorderRequest){
            TestStep parentWhileStep = testStep.getParentStep();
            repository.delete(parentWhileStep);
        }
        repository.delete(testStep);
        if (testStep.getAddonActionId() != null) {
            AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(testStep.getAddonActionId());
            addonService.notifyActionNotUsing(addonNaturalTextAction);
        }
        publishEvent(testStep, EventType.DELETE);
    }

    public TestStep find(Long id) throws ResourceNotFoundException {
        return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestStep missing with id:" + id));
    }

    private TestStep updateDetails(TestStep testStep, Boolean isRecorderRequest) {
        RestStep restStep = testStep.getRestStep();
        testStep.setRestStep(null);
        if(isRecorderRequest)
            handleWhileTestStepUpdate(testStep);
        testStep = this.repository.save(testStep);
        if (restStep != null) {
            restStep.setStepId(testStep.getId());
            restStep = this.restStepService.update(restStep);
            testStep.setRestStep(restStep);
        }
        return testStep;
    }

    public TestStep update(TestStep testStep, Boolean isRecorderRequest) throws TestsigmaException {
        if (testStep.getConditionType()==TestStepConditionType.LOOP_WHILE
                && testStep.getMaxIterations() != null
                && testStep.getMaxIterations()>100){
            throw  new TestsigmaException(String.format("In While Loop, please set Max iterations between 1 to 100"));
        }
        testStep = updateDetails(testStep, isRecorderRequest);
        this.updateDisablePropertyForChildSteps(testStep, isRecorderRequest);
        publishEvent(testStep, EventType.UPDATE);
        return testStep;
    }

    private void updateDisablePropertyForChildSteps(TestStep testStep, Boolean isRecorderRequest) throws TestsigmaException {
        List<TestStep> childSteps = this.repository.findAllByParentIdOrderByPositionAsc(testStep.getId());
        if (childSteps.size() > 0) {
            for (TestStep childStep : childSteps) {
                childStep.setDisabled(testStep.getDisabled());
                this.update(childStep, isRecorderRequest);
            }
        }
    }


    public TestStep create(TestStep testStep, Boolean isRecorderRequest) throws TestsigmaException,ResourceNotFoundException{
        if(testStep.getAction()!=null
                && testStep.getConditionType() == TestStepConditionType.LOOP_WHILE
                &&(testStep.getMaxIterations() != null && (testStep.getMaxIterations() > 100))){
            throw  new TestsigmaException(String.format("In While Loop, please set Max iterations between 1 to 100"));
        }
        setTestStepPosition(testStep);
        this.repository.incrementPosition(testStep.getPosition(), testStep.getTestCaseId());
        RestStep restStep = testStep.getRestStep();
        testStep.setRestStep(null);
        if(isRecorderRequest) {
            testStep = this.handleWhileTestStepCreate(testStep);
        }
        testStep = this.repository.save(testStep);
        if (restStep != null) {
            RestStep newRestStep = mapper.mapStep(restStep);
            newRestStep.setStepId(testStep.getId());
            newRestStep = this.restStepService.create(newRestStep);
            testStep.setRestStep(newRestStep);
        }
        if (testStep.getAddonActionId() != null) {
            AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(testStep.getAddonActionId());
            addonService.notifyActionUsing(addonNaturalTextAction);
        }
        publishEvent(testStep, EventType.CREATE);
        return testStep;
    }

    private void setTestStepPosition(TestStep testStep) {
        if (testStep.getPosition() == null) {
            Optional<TestStep> lastStep = this.repository.findFirstByTestCaseIdOrderByPositionDesc(testStep.getTestCaseId());
            testStep.setPosition(lastStep.map(step -> step.getPosition() + 1).orElse(0));
        }
    }

    public TestStep handleWhileTestStepCreate(TestStep testStep) throws TestsigmaException {
        if(TestStepConditionType.LOOP_WHILE == testStep.getConditionType()){
            TestStep parentWhileStep = testStepMapper.copy(testStep);
            parentWhileStep.setParentId(testStep.getParentId());
            parentWhileStep.setParentStep(testStep.getParentStep());
            parentWhileStep.setConditionType(null);
            parentWhileStep.setType(TestStepType.WHILE_LOOP);
            parentWhileStep.setNaturalTextActionId(null);
            parentWhileStep.setAction(null);
            TestStepDataMap parentWhileStepMap = new TestStepDataMap();
            parentWhileStepMap.setWhileCondition("");
            parentWhileStep.setDataMap(parentWhileStepMap);
            parentWhileStep = create(parentWhileStep, true);
            testStep.setParentStep(parentWhileStep);
            testStep.setParentId(parentWhileStep.getId());
            testStep.setPosition(parentWhileStep.getPosition()+1);
        }
        return testStep;
    }

    public List<TestStepDTO> filterWhileParentSteps(List<TestStepDTO> testSteps){
        List<TestStepDTO> newTestSteps = new ArrayList<>();
        for(TestStepDTO testStep: testSteps){
            if(testStep.getConditionType() == TestStepConditionType.LOOP_WHILE){
                Long parentWhileId = testStep.getParentId();
                if(parentWhileId!=null){
                    TestStep parentWhileStep = repository.getById(parentWhileId);
                    if(parentWhileStep.getParentId()!=null){
                        testStep.setParentId(parentWhileStep.getParentId());
                    }
                }
            }
            if(testStep.getType() != TestStepType.WHILE_LOOP)
                newTestSteps.add(testStep);
        }
        setMainParentIDForTestStepDTOs(newTestSteps);
        return newTestSteps;
    }

    public void handleWhileTestStepUpdate(TestStep testStep){
        // Updating disabled property and timeout and parent step to the
        if(testStep.getConditionType() == TestStepConditionType.LOOP_WHILE){
            TestStep parentWhileStep = testStep.getParentStep();
            if(parentWhileStep.getId() != testStep.getParentId())
                parentWhileStep.setParentId(testStep.getParentId());
            parentWhileStep.setDisabled(testStep.getDisabled());
            parentWhileStep.setWaitTime(testStep.getWaitTime());
            parentWhileStep = this.repository.save(parentWhileStep);
            testStep.setParentId(parentWhileStep.getId());
        }
    }

    public void setMainParentIDForTestStepDTOs(List<TestStepDTO> testStepDTOs){
        for(TestStepDTO testStepDTO:testStepDTOs){
            setMainParentIDForTestStepDTO(testStepDTO);
        }
    }

    public void setMainParentIDForTestStepDTO(TestStepDTO testStepDTO) {
        if (testStepDTO.getConditionType() == TestStepConditionType.LOOP_WHILE) {
            TestStep parentStep = repository.getById(testStepDTO.getParentId());
            if (parentStep.getParentId() != null) {
                testStepDTO.setParentId(parentStep.getParentId());
            } else {
                testStepDTO.setParentId(null);
            }
        }
    }

    public void bulkUpdateProperties(Long[] ids, TestStepPriority testStepPriority, Integer waitTime, Boolean disabled,
                                     Boolean ignoreStepResult,Boolean visualEnabled, Boolean isRecorderRequest) {
        this.repository.bulkUpdateProperties(ids, testStepPriority != null ? testStepPriority.toString() : null, waitTime,visualEnabled);
        if (disabled != null || ignoreStepResult != null)
            this.bulkUpdateDisableAndIgnoreResultProperties(ids, disabled, ignoreStepResult, isRecorderRequest);
    }

    private void bulkUpdateDisableAndIgnoreResultProperties(Long[] ids, Boolean disabled, Boolean ignoreStepResult, Boolean isRecorderRequest) {
        List<TestStep> testSteps = this.repository.findAllByIdInOrderByPositionAsc(ids);
        for (TestStep testStep : testSteps) {
            if (disabled != null) {
                if (!disabled && testStep.getParentStep() == null) {
                    testStep.setDisabled(false);
                } else if (!disabled && testStep.getParentStep() != null) {
                    testStep.setDisabled(testStep.getParentStep().getDisabled());
                } else {
                    testStep.setDisabled(disabled);
                }
            }
            if (ignoreStepResult != null) {
                testStep.setIgnoreStepResult(ignoreStepResult);
            }
            this.updateDetails(testStep, isRecorderRequest);
        }
    }


    public List<TestStep> findAllByTestCaseIdAndIdIn(Long testCaseId, List<Long> stepIds) {
        return this.repository.findAllByTestCaseIdAndIdInOrderByPosition(testCaseId, stepIds);
    }

    public void updateTestDataParameterName(Long testDataId, String parameter, String newParameterName) {
        List<TestStep> testSteps = this.repository.getTopLevelTestDataParameter(testDataId);
        for(TestStep testStep : testSteps) {
            TestStepDataMap testStepData = testStep.getDataMap();
            if(testStepData != null && testStepData.getTestData() != null) {
                testStepData.getTestData().entrySet().forEach(stringTestStepNlpDataEntry -> {
                    if(stringTestStepNlpDataEntry.getValue().getValue().equals(parameter)){
                        stringTestStepNlpDataEntry.getValue().setValue(newParameterName);
                    }
                });
            }
            TestStepDataMap map = testStep.getDataMap();
            map.setTestData(testStepData.getTestData());
            testStep.setDataMap(map);
            save(testStep);
        }
        List<TestStep> topConditionalSteps = this.repository.getTopLevelConditionalStepsExceptLoop(testDataId);
        for (TestStep step : topConditionalSteps) {
            updateChildLoops(step.getId(), parameter, newParameterName);
        }
        List<TestStep> loopSteps = this.repository.getAllLoopSteps(testDataId);
        for (TestStep step : loopSteps) {
            updateChildLoops(step.getId(), parameter, newParameterName);
        }
    }

    public void updateElementName(String oldName, String newName, Long workspaceVersionId) {
        this.repository.updateElementName(newName, oldName,workspaceVersionId);
    }

    private void updateChildLoops(Long parentId, String parameter, String newParameterName) {
        List<TestStep> childSteps = this.repository.getChildStepsTestDataParameter(parentId);
        childSteps.addAll(this.repository.getChildStepsTestDataParameterUsingTestDataProfileId(parentId));
        for(TestStep testStep : childSteps) {
            TestStepDataMap testStepData = testStep.getDataMap();
            if(testStepData != null && testStepData.getTestData() != null) {
                testStepData.getTestData().entrySet().forEach(stringTestStepNlpDataEntry -> {
                    if(stringTestStepNlpDataEntry.getValue().getValue().equals(parameter)){
                        stringTestStepNlpDataEntry.getValue().setValue(newParameterName);
                    }
                });
            }
            TestStepDataMap map = testStep.getDataMap();
            map.setTestData(testStepData.getTestData());
            testStep.setDataMap(map);
            save(testStep);
        }
        List<TestStep> conditionalSteps = this.repository.getChildConditionalStepsExceptLoop(parentId);
        for (TestStep step : conditionalSteps) {
            updateChildLoops(step.getId(), parameter, newParameterName);
        }
    }

    public List<TestStep> findAllByTestCaseIdAndNaturalTextActionIds(Long id, List<Integer> ids) {
        return this.repository.findAllByTestCaseIdAndNaturalTextActionIdIn(id, ids);
    }

    public Integer countAllByAddonActionIdIn(List<Long> ids) {
        return this.repository.countAllByAddonActionIdIn(ids);
    }

    public void updateAddonElementsName(String oldName, String newName,Long workspaceVersionId) {
        List<TestStep> testSteps = this.repository.findAddonElementsByName(oldName,workspaceVersionId);
        testSteps.forEach(testStep -> {
            Map<String, AddonElementData> elements = testStep.getAddonElements();
            for (Map.Entry<String, AddonElementData> entry : elements.entrySet()) {
                if (entry.getValue().getName().equals(oldName)) {
                    entry.getValue().setName(newName);
                }
            }
            testStep.setAddonElements(elements);
            this.repository.save(testStep);
        });
    }

    public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
        if (!backupDTO.getIsTestStepEnabled()) return;
        log.debug("backup process for test step initiated");
        writeXML("test_steps", backupDTO, PageRequest.of(0, 25));
        log.debug("backup process for test step completed");
    }

    public Specification<TestStep> getExportXmlSpecification(BackupDTO backupDTO) {
        List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
        List<Long> testcaseIds = testCaseList.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
        SearchCriteria criteria = new SearchCriteria("testCaseId", SearchOperation.IN, testcaseIds);
        List<SearchCriteria> params = new ArrayList<>();
        params.add(criteria);
        TestStepSpecificationsBuilder testStepSpecificationsBuilder = new TestStepSpecificationsBuilder();
        testStepSpecificationsBuilder.params = params;
        return testStepSpecificationsBuilder.build();
    }

    @Override
    protected List<TestStepXMLDTO> mapToXMLDTOList(List<TestStep> list) {
        return exportTestStepMapper.mapTestSteps(list);
    }

    public void publishEvent(TestStep testSuite, EventType eventType) {
        TestStepEvent<TestStep> event = createEvent(testSuite, eventType);
        log.info("Publishing event - " + event.toString());
        applicationEventPublisher.publishEvent(event);
    }

    public TestStepEvent<TestStep> createEvent(TestStep testSuite, EventType eventType) {
        TestStepEvent<TestStep> event = new TestStepEvent<>();
        event.setEventData(testSuite);
        event.setEventType(eventType);
        return event;
    }

    private void generateXLSheet(BackupDTO importDTO){
        HashMap<TestStep, String> stepsMap= this.affectedTestCaseXLSExportService.getStepsMap();
        if (stepsMap !=null && !stepsMap.isEmpty()) {
            XSSFWorkbook workbook = this.affectedTestCaseXLSExportService.initializeWorkBook();
            try {
                this.affectedTestCaseXLSExportService.addToExcelSheet(stepsMap, workbook, importDTO, 1);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
        if (!importDTO.getIsTestStepEnabled()) return;
        log.debug("import process for Test step initiated");
        this.affectedTestCaseXLSExportService.setStepsMap(new HashMap<>());
        importFiles("test_steps", importDTO);
        generateXLSheet(importDTO);
        log.debug("import process for Test step completed");
    }

    @Override
    public List<TestStep> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
        if (importDTO.getIsCloudImport()) {
            xmlData = xmlData.replaceAll("API_STEP", "REST_STEP");
            List<TestStep> steps = mapper.mapTestStepsCloudList(xmlMapper.readValue(xmlData, new TypeReference<List<TestStepCloudXMLDTO>>() {
            }));
            HashMap<TestStep, String> stepsMap= this.affectedTestCaseXLSExportService.getStepsMap();
            for (TestStep step : steps) {
                String message =null;
                try {
                    if (step.getAddonActionId() != null && step.getAddonActionId() > 0) {
                        message = "Not supported Addon Step!";
                        addonNaturalTextActionService.findById(step.getAddonActionId());
                    }
                    if (step.getNaturalTextActionId() != null && step.getNaturalTextActionId() > 0) {
                        if (step.getNaturalTextActionId()==574){   //// TODO: need to changes on Cloud side [Siva Nagaraju]
                            step.setNaturalTextActionId(1038);
                            continue;
                        }
                        message = "Deprecated Action!";
                        try {
                        naturalTextActionsService.findById(Long.valueOf(step.getNaturalTextActionId()));
                        }
                        catch (Exception e){
                            if (this.depreciatedIds.contains(step.getNaturalTextActionId()))
                                this.mapDeprecatedActionsWithUpdatesOnes(step);
                            else {
                                log.error(e.getMessage() + " and Not able to Map this Action Id with the Mapper");
                                step.setDisabled(true);
                                step.setAddonActionId(null);
                                stepsMap.put(step, message);
                            }
                        }
                    }
                    if (step.getDataMap().getTestData() != null && Objects.equals(step.getDataMap().getTestData().get("test-data").getType(), TestDataType.function.getDispName()) && step.getType() != TestStepType.CUSTOM_FUNCTION) {
                        message = "Deprecated Data Generator / Data Generator not found!";
                        defaultDataGeneratorService.find(step.getTestDataFunctionId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    step.setDisabled(true);
                    step.setAddonActionId(null);
                    stepsMap.put(step, message);
                    log.info("disabling test steps having template id or addon action id or custom function id is not found!");
                }
                if (step.getType() == TestStepType.CUSTOM_FUNCTION) {
                    step.setDisabled(true);
                    stepsMap.put(step, "Custom Functions not supported in OS");
                    log.info("disabling Custom function test step to avoid further issues, since CSFs are deprecated");
                }
                if (step.getType() == TestStepType.FOR_LOOP) {
                    Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, step.getForLoopTestDataId());
                    if (testData.isPresent())
                        step.setForLoopTestDataId(testData.get().getId());
                }
            }
            return steps;
        } else {
            List<TestStep> steps = mapper.mapTestStepsList(xmlMapper.readValue(xmlData, new TypeReference<List<TestStepXMLDTO>>() {
            }));
            for (TestStep step : steps) {
                if (step.getTestDataProfileStepId() != null) {
                    Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, step.getTestDataProfileStepId());
                    testData.ifPresent(data -> step.setTestDataProfileStepId(data.getId()));
                }
                if (step.getType() == TestStepType.FOR_LOOP) {
                    Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, step.getForLoopTestDataId());
                    testData.ifPresent(data -> step.setForLoopTestDataId(data.getId()));
                }
            }
            return steps;
        }
    }


    private void mapDeprecatedActionsWithUpdatesOnes(TestStep step) {
        ActionTestDataMap filteredMap = this.actionTestDataMap.stream().filter(dataMap -> dataMap.getTestDataHash().containsKey(step.getNaturalTextActionId())).findFirst().orElse(null);
        if (filteredMap != null) {
            TestStepDataMap testStepData = step.getDataMap() != null ? new TestStepDataMap() : step.getDataMap();
            if(testStepData.getTestData() == null) {
                testStepData.setTestData(new HashMap<>());
            }
            TestStepNlpData testStepNlpData = new TestStepNlpData();
            testStepNlpData.setValue(filteredMap.getTestDataHash().get(step.getNaturalTextActionId()));
            testStepNlpData.setType(TestDataType.raw.getDispName());
            testStepData.getTestData().put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, testStepNlpData);
            step.setNaturalTextActionId(filteredMap.getOptimizedActionId());
        }
    }


    @Override
    public Optional<TestStep> findImportedEntity(TestStep testStep, BackupDTO importDTO) {
        Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, testStep.getTestCaseId());
        if (testCase.isEmpty()) {
            return Optional.empty();
        }
        Optional<TestStep> previous = repository.findByTestCaseIdInAndImportedId(List.of(testCase.get().getId()), testStep.getId());
        return previous;
    }

    @Override
    public TestStep processBeforeSave(Optional<TestStep> previous, TestStep present, TestStep toImport, BackupDTO importDTO) throws ResourceNotFoundException {
        present.setImportedId(present.getId());
        if (previous.isPresent() && importDTO.isHasToReset()) {
            present.setId(previous.get().getId());
        } else {
            present.setId(null);
        }
        setTemplateId(present, importDTO);
        processTestDataMap(present, importDTO);
        Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, present.getTestCaseId());
        if (testCase.isPresent())
            present.setTestCaseId(testCase.get().getId());
        if (present.getStepGroupId() != null) {
            Optional<TestCase> testComponent = testCaseService.getRecentImportedEntity(importDTO, present.getStepGroupId());
            if (testComponent.isPresent())
                present.setStepGroupId(testComponent.get().getId());
        }
        if (present.getParentId() != null) {
            Optional<TestStep> testStep = getRecentImportedEntity(importDTO, present.getParentId());
            if (testStep.isPresent()) {
                present.setParentId(testStep.get().getId());
                if (testStep.get().getDisabled()) {
                    present.setDisabled(true);
                }
            }

        }

        if (present.getPreRequisiteStepId() != null) {
            Optional<TestStep> recentPrerequisite = getRecentImportedEntityForPreq(present.getTestCaseId(), present.getPreRequisiteStepId());
            if (recentPrerequisite.isPresent())
                present.setPreRequisiteStepId(recentPrerequisite.get().getId());
        }
        return present;
    }

    public boolean hasToSkip(TestStep testStep, BackupDTO importDTO) {
        Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, testStep.getTestCaseId());
        return testCase.isEmpty();
    }

    @Override
    void updateImportedId(TestStep testStep, TestStep previous, BackupDTO importDTO) {
        previous.setImportedId(testStep.getId());
        save(previous);
    }

    private void processTestDataMap(TestStep present, BackupDTO importDTO) {
        TestStepDataMap testStepDataMap = present.getDataMapBean();
        if (testStepDataMap != null) {

            if ((testStepDataMap.getForLoop() != null || testStepDataMap.getWhileCondition() != null)
                    && testStepDataMap.getForLoop() != null && testStepDataMap.getForLoop().getTestDataId() != null) {
                Optional<TestData> testData = testDataService.getRecentImportedEntity(importDTO, testStepDataMap.getForLoop().getTestDataId());
                if (testData.isPresent())
                    testStepDataMap.getForLoop().setTestDataId(testData.get().getId());
            }
        }

    }

    private void setTemplateId(TestStep present, BackupDTO importDTO) throws ResourceNotFoundException {
        if (!importDTO.getIsSameApplicationType() && present.getNaturalTextActionId() != null && present.getNaturalTextActionId() > 0) {
            try {
                NaturalTextActions nlpTemplate = naturalTextActionsService.findById(present.getNaturalTextActionId().longValue());
                if (importDTO.getWorkspaceType().equals(WorkspaceType.WebApplication)) {
                    present.setNaturalTextActionId(nlpTemplate.getImportToWeb().intValue());
                    if (nlpTemplate.getImportToWeb().intValue() == 0) {
                        present.setDisabled(true);
                    }
                } else if (importDTO.getWorkspaceType().equals(WorkspaceType.MobileWeb)) {
                    present.setNaturalTextActionId(nlpTemplate.getImportToMobileWeb().intValue());
                    if (nlpTemplate.getImportToMobileWeb().intValue() == 0) {
                        present.setDisabled(true);
                    }
                } else if (importDTO.getWorkspaceType().equals(WorkspaceType.AndroidNative)) {
                    present.setNaturalTextActionId(nlpTemplate.getImportToAndroidNative().intValue());
                    if (nlpTemplate.getImportToAndroidNative().intValue() == 0) {
                        present.setDisabled(true);
                    }
                } else if (importDTO.getWorkspaceType().equals(WorkspaceType.IOSNative)) {
                    present.setNaturalTextActionId(nlpTemplate.getImportToIosNative().intValue());
                    if (nlpTemplate.getImportToIosNative().intValue() == 0) {
                        present.setDisabled(true);
                    }
                }
            } catch (Exception e) {
                log.debug("mapping failed for templateId   " + present.getNaturalTextActionId().longValue());
                present.setNaturalTextActionId(0);
                present.setDisabled(true);
            }
        }
    }

    @Override
    public TestStep copyTo(TestStep testStep) {
        return mapper.copy(testStep);
    }

    @Override
    public TestStep save(TestStep testStep) {
        return repository.save(testStep);
    }

    @Override
    public Optional<TestStep> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
        Long importedId = ids[0];
        List<Long> testcaseIds = new ArrayList<>();
        testCaseService.findAllByWorkspaceVersionId(importDTO.getWorkspaceVersionId()).stream().forEach(testCase -> testcaseIds.add(testCase.getId()));
        Optional<TestStep> previous = repository.findByTestCaseIdInAndImportedId(testcaseIds, importedId);
        return previous;
    }

    public Optional<TestStep> getRecentImportedEntityForPreq(Long testcaseId, Long importedId) {
        Optional<TestStep> previous = repository.findAllByTestCaseIdAndImportedId(testcaseId, importedId);
        return previous;
    }

    public Optional<TestStep> findImportedEntityHavingSameName(Optional<TestStep> previous, TestStep current, BackupDTO importDTO) {
        return previous;
    }

    public boolean hasImportedId(Optional<TestStep> previous) {
        return previous.isPresent() && previous.get().getImportedId() != null;
    }

    public boolean isEntityAlreadyImported(Optional<TestStep> previous, TestStep current) {
        return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
    }

    public List<TestStep> findAllByTestCaseIdIn(List<Long> testCaseIds) {
        return this.repository.findAllByTestCaseIdInOrderByPositionAsc(testCaseIds);
    }

    private List<ActionTestDataMap> getMapsList(){
        List<ActionTestDataMap> actionsMap = new ArrayList<>();
        actionsMap.add(new ActionTestDataMap(WorkspaceType.WebApplication, 1080, DeprecatedActionMapper.getWebWaitMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10192, DeprecatedActionMapper.getMobileWebWaitMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20153, DeprecatedActionMapper.getAndroidWaitMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30147, DeprecatedActionMapper.getIOSWaitMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.WebApplication, 1079, DeprecatedActionMapper.getWebVerifyMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10191, DeprecatedActionMapper.getMobileWebVerifyMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20152, DeprecatedActionMapper.getAndroidVerifyMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30146, DeprecatedActionMapper.getIOSVerifyMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10196, DeprecatedActionMapper.getMobileWebTapOnAlertMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20156, DeprecatedActionMapper.getAndroidTapOnAlertMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30151, DeprecatedActionMapper.getIOSTapOnAlertMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10190, DeprecatedActionMapper.getMobileWebSwipeMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20150, DeprecatedActionMapper.getAndroidSwipeFromMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30144, DeprecatedActionMapper.getIOSSwipeFromMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20151, DeprecatedActionMapper.getAndroidSwipeToMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30145, DeprecatedActionMapper.getIOSSwipeToMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20154, DeprecatedActionMapper.getAndroidEnableSwitchMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30148, DeprecatedActionMapper.getIOSEnableSwitchMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10195, DeprecatedActionMapper.getMobileWebTapOnKeyMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.AndroidNative, 20155, DeprecatedActionMapper.getAndroidTapOnKeyMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30150, DeprecatedActionMapper.getIOSTapOnKeyMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.WebApplication, 1082, DeprecatedActionMapper.getMobileWebScrollInsideElementMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10194, DeprecatedActionMapper.getMobileWebScrollInsideElementMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.WebApplication, 1081, DeprecatedActionMapper.getWebScrollToElementMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10193, DeprecatedActionMapper.getMobileWebScrollToElementMap()));

        actionsMap.add(new ActionTestDataMap(WorkspaceType.WebApplication, 1083, DeprecatedActionMapper.getWebClickOnButtonMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.MobileWeb, 10197, DeprecatedActionMapper.getMobileWebTapOnButtonMap()));
        actionsMap.add(new ActionTestDataMap(WorkspaceType.IOSNative, 30149, DeprecatedActionMapper.getIOSWIFISwitchMap()));

        return actionsMap;
    }
    public void bulkDelete(Long[] testStepIds) throws ResourceNotFoundException {
        for (Long id:testStepIds){
              this.destroy(this.find(id), false);
            }
    }

    /**
     * Returns a boolean value true whether testStepList.id is a subset of testStepsIds
     * @param testStepsIds
     * @param testStepList
     * @return {boolean}
     */
    private Boolean isTestStepsSubsetOfIds(List<Long> testStepsIds, List<TestStep> testStepList){
        for (TestStep testStep:testStepList){
            if(!testStepsIds.contains(testStep.getId())){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a List of TestStepsDTO which having Prerequisite steps and the linked testSteps <br/>
     * <ol>
     *     <li>Iterating over the testStepsIds to know wether they are having any associated steps</li>
     *     <li>If there are any testSteps that are having prerequiste as Current iterating Step Id then the <b>isTestStepsSubsetOfIds</b> method will check wether all the testSteps are going to be deleted or not</li>
     *     <li>Then the main step <b>i.e</b> Step that have been selected as prerequiste step to other steps , All these steps will be added to a list in an order</li>
     *     <li>Order of adding steps to list is <i>Add the main step followed by the associated steps</i></li>
     * </ol>
     * @param testStepsIds An array of Ids of the teststeps which are the part of the bultTestStepsDelete Operation
     * @return {List<TestStepDTO>}
     * @throws ResourceNotFoundException
     */
    public List<TestStepDTO> indexTestStepsHavingPrerequisiteSteps(Long[] testStepsIds) throws ResourceNotFoundException {
        Long testCaseId = this.find(testStepsIds[0]).getTestCaseId();
        List<TestStepDTO> testSteps = new ArrayList<>();
        List<Long> idsList=Arrays.asList(testStepsIds);
        int index=0;
        for(Long id: testStepsIds) {
            List<TestStep> testStepList= this.repository.findAllByTestCaseIdAndPreRequisiteStepId(testCaseId,id);
            if(!testStepList.isEmpty() && !isTestStepsSubsetOfIds(idsList,testStepList)) {
                TestStep mainStep = this.find(id);
                TestStepDTO testStepDTO = this.exportTestStepMapper.mapDTO(mainStep);
                testStepDTO.setPreRequisiteStepId(null);
                testSteps.add(index++,testStepDTO);
                for(TestStep testStep:testStepList){
                    testSteps.add(index++,this.exportTestStepMapper.mapDTO(testStep));
                }
            }
        }
        return testSteps;
    }

    public List<TestStep> findAllByWorkspaceVersionIdAndNaturalTextActionId(Long workspaceVersionId, List<Integer> naturalTextActionIds) {
        return this.repository.findAllByWorkspaceVersionIdAndNaturalTextActionId(workspaceVersionId, naturalTextActionIds);
    }

    public List<TestStep> findAllRuntimeDataRestStep(Long workspaceVersionId) {
        return  this.repository.getAllRestStepWithRuntime(workspaceVersionId);
    }

    public void deleteStepsByTestCaseId(Long testCaseId){
        repository.deleteStepsByTestCaseId(testCaseId);
    }

    public Optional<TestStep> findTopByTestCaseIdOrderByPositionDesc(Long testCaseId) {
        return this.repository.findTopByTestCaseIdOrderByPositionDesc(testCaseId);
    }

    public void deleteStepsByStepGroupId(Long stepGroupId){
        repository.deleteStepsByStepGroupId(stepGroupId);
    }

}

@Data
@AllArgsConstructor
class ActionTestDataMap {
    WorkspaceType workspaceType;
    Integer optimizedActionId;
    HashMap<Integer, String> testDataHash;
}
