package com.testsigma.service.testproject;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.web.request.testproject.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestStepImportService extends BaseImportService<TestProjectTestStepRequest> {

    private final TestCaseService testCaseService;
    private final TestStepService testStepService;
    private final ElementsImportService elementsImportService;
    private final EntityExternalMappingService entityExternalMappingService;
    private final ExternalImportNlpMappingService externalNlpMappingService;
    private final ElementService elementService;
    private final WorkspaceService workspaceService;

    private TestProjectYamlRequest projectRequest;
    private TestProjectTestCaseRequest testCaseRequest;
    private Workspace workspace;
    private WorkspaceVersion workspaceVersion;
    private Integrations integration;
    private Map<String, String> projectParametersMap;
    private Map<String, String> testCaseParametersMap;

    //TODO: Revisit this when we have multiple test data support
    public void importSteps(TestProjectYamlRequest projectRequest,
                            TestCase testCase,
                            TestProjectTestCaseRequest testCaseRequest,
                            WorkspaceVersion workspaceVersion,
                            Integrations integration,
                            Boolean isStepGroup) throws ResourceNotFoundException, TestProjectImportException {
        this.projectRequest = projectRequest;
        this.testCaseRequest = testCaseRequest;
        this.workspaceVersion = workspaceVersion;
        this.integration = integration;
        this.projectParametersMap = new HashMap<>();
        this.testCaseParametersMap = new HashMap<>();
        this.workspace = this.workspaceService.find(this.workspaceVersion.getWorkspaceId());
        int stepPosition = 0;
        for(TestProjectTestStepRequest stepRequest : testCaseRequest.getSteps()){
            importStepIntoTestCase(stepRequest, testCase, stepPosition++, isStepGroup);
        }
    }

    private void importStepIntoTestCase(TestProjectTestStepRequest stepRequest,
                                        TestCase testCase, Integer initialStepId,
                                        Boolean isStepGroup) throws TestProjectImportException, ResourceNotFoundException {
        createTestStep(stepRequest, testCase, initialStepId, isStepGroup);
    }



    private void createTestStep(TestProjectTestStepRequest stepRequest,
                                TestCase testCase, Integer position,
                                Boolean isStepGroup) throws TestProjectImportException, ResourceNotFoundException {
        TestStep testStep = new TestStep();
        TestProjectNLP testProjectNLPs = new TestProjectNLP();
        populateParams();
        boolean nlpMappingNotFound = false;
        if(getTemplateId(stepRequest) != null)
            testStep.setNaturalTextActionId(getTemplateId(stepRequest));
        else if(stepRequest.getStepType() == TestStepType.STEP_GROUP)
            checkAndAddStepGroup(stepRequest, testStep);
        else
            nlpMappingNotFound = true;
        setDisabled(stepRequest, testStep, nlpMappingNotFound);
        testStep.setTestCaseId(testCase.getId());
        setStepTimeOut(stepRequest, testStep);
        setIgnoreAndStepPriority(stepRequest, testStep);
        testStep.setType(stepRequest.getStepType());
        setTestDataIfExists(stepRequest, testStep, isStepGroup);
        createElementIfPresent(stepRequest,testCase.getWorkspaceVersionId(), testStep);
        testStep.setPosition(position);
        if(stepRequest.getStepType() == TestStepType.ACTION_TEXT){
            if(stepRequest.getAction() != null){
                String action = testProjectNLPs.getNlpByIdAndType(stepRequest.getAction().getId(), this.workspace.getWorkspaceType()).getDescription();
                String replacedAction = replaceActionWithParams(action, stepRequest, testStep, isStepGroup);
                testStep.setAction(replacedAction);
            }
            else
                log.info("Action not found for step id - " + stepRequest.getId());
        }
        testStepService.save(testStep);
    }

    private void populateParams() {
        for(TestProjectGlobalParametersRequest parameter : this.projectRequest.getProjectParameters()) {
            this.projectParametersMap.put(parameter.getName(), parameter.getValue());
        }
        this.projectRequest.getTests().forEach(test -> test.getParameters().forEach(testCaseParam -> {{
            this.testCaseParametersMap.put(testCaseParam.getName(), testCaseParam.getValue());
        }}));
    }

    private String replaceActionWithParams(String action, TestProjectTestStepRequest stepRequest,
                                           TestStep testStep,
                                           Boolean isStepGroup) throws ResourceNotFoundException, TestProjectImportException {
        action = replaceLocalParameters(action, stepRequest, testStep);
        if(isStepGroup) {
            for(TestProjectStepParameter parameter : this.testCaseRequest.getParameters()) {
                if(action.contains("{{" + parameter.getName() + "}}")) {
                    action = action.replace("{{" + parameter.getName() + "}}", parameter.getValue());
                    testStep.setDisabled(parameter.getValue() == null || Boolean.TRUE.equals(testStep.getDisabled()));
                }
            }
        } else {
            action = replaceTestCaseParameters(action, testStep);
        }
        action = replaceGlobalParameters(action, testStep);
        if(stepRequest.getElementId() != null) {
            List<EntityExternalMapping> entityExternalMapping = entityExternalMappingService.findByExternalIdAndEntityTypeAndApplicationId(testStep.getElement(), EntityType.ELEMENT, integration.getId());
            Element element = elementService.find(entityExternalMapping.get(0).getEntityId());
            String elementName = element.getName();
            action = action.replace("{{element}}", elementName);
        }
        return action;
    }

    private void checkAndAddStepGroup(TestProjectTestStepRequest stepRequest, TestStep testStep) throws ResourceNotFoundException {
        String stepGroupId = stepRequest.getTargetTestId();
        List<EntityExternalMapping> entityExternalMapping = entityExternalMappingService.findByExternalIdAndEntityTypeAndApplicationId(stepGroupId, EntityType.TEST_CASE, this.integration.getId());
        if(!entityExternalMapping.isEmpty()) {
            TestCase stepGroup = testCaseService.find(entityExternalMapping.get(0).getEntityId());
            testStep.setStepGroupId(stepGroup.getId());
        }
    }

    private Integer getTemplateId(TestProjectTestStepRequest stepRequest){
        //If Action is null, that means it's a StepGroup
        if(stepRequest.getAction() == null || stepRequest.getAction().getId() == null)
            return null;
        String actionId = stepRequest.getAction().getId();
        Optional<ExternalImportNlpMapping> nlpMapping =
                externalNlpMappingService.findByExternalIdAndExternalImportTypeAndWorkspaceType(actionId,
                            ExternalImportType.TEST_PROJECT, this.workspace.getWorkspaceType());
        if (nlpMapping.isPresent()) {
            return nlpMapping.get().getTestsigmaNlpId();
        }
        return null;
    }

    private void setTestDataIfExists(TestProjectTestStepRequest stepRequest, TestStep testStep, Boolean isStepGroup){
        testStep.setTestDataType(TestDataType.raw.name());
        if(!stepRequest.getParameterMaps().isEmpty()) {
            String stepTestData = stepRequest.getParameterMaps().get(0).getValue();
            if(stepTestData.startsWith("[[")){
                stepTestData = stepTestData.substring(2,stepTestData.length()-2);
                if(this.projectParametersMap.containsKey(stepTestData)) {
                    stepTestData = this.projectParametersMap.get(stepTestData);
                }
                testStep.setTestDataType(TestDataType.raw.name());
            } else if(stepTestData.startsWith("{{") && isStepGroup) {
                stepTestData = stepTestData.substring(2,stepTestData.length()-2);
                for(TestProjectStepParameter parameter : this.testCaseRequest.getParameters()) {
                    if(parameter.getName().equals(stepTestData)) {
                        stepTestData = parameter.getValue();
                    }
                }
            }
            else if(stepTestData.startsWith("{{") && !isStepGroup) {
                stepTestData = stepTestData.substring(2,stepTestData.length()-2);
                if(this.testCaseParametersMap.containsKey(stepTestData)) {
                    stepTestData = this.testCaseParametersMap.get(stepTestData);
                }
                testStep.setTestDataType(TestDataType.raw.name());
            }
            testStep.setTestData(stepTestData);
        }
    }

    private void createElementIfPresent(TestProjectTestStepRequest stepRequest, Long applicationVersionId, TestStep testStep) throws ResourceNotFoundException {
        String elementId = stepRequest.getElementId();
        if(elementId != null){
            TestProjectElementRequest elementRequest = findElementByIdInProject(elementId);
            Element element = elementsImportService.createElementObject(elementRequest, applicationVersionId, this.integration);
            String locatorValue = replaceLocalParameters(element.getLocatorValue(), stepRequest, testStep);
            locatorValue = replaceTestCaseParameters(locatorValue, testStep);
            locatorValue = replaceGlobalParameters(locatorValue, testStep);
            element.setLocatorValue(locatorValue);
            element = elementService.create(element);
            List<EntityExternalMapping> entityExternalMapping = entityExternalMappingService.findByExternalIdAndEntityTypeAndApplicationId(elementRequest.getName(), EntityType.ELEMENT, integration.getId());
            if(entityExternalMapping.isEmpty()) {
                createEntityExternalMappingIfNotExists(element.getName(), EntityType.ELEMENT, element.getId(), integration);
            }
            testStep.setElement(element.getName());
        }
    }

    private String replaceLocalParameters(String str, TestProjectTestStepRequest stepRequest, TestStep testStep) {
        List<TestProjectStepParameter> parameters = stepRequest.getParameterMaps();
        for(TestProjectStepParameter parameter : parameters){
            if(str.contains("{{" + parameter.getName() + "}}")) {
                str = str.replace("{{" + parameter.getName() + "}}", parameter.getValue());
                testStep.setDisabled(parameter.getValue() == null || Boolean.TRUE.equals(testStep.getDisabled()));
            }
        }
        return str;
    }

    private String replaceTestCaseParameters(String str, TestStep testStep) {
        for(String key : this.testCaseParametersMap.keySet()) {
            if(str.contains("{{" + key + "}}")) {
                str = str.replace("{{" + key + "}}", this.testCaseParametersMap.get(key));
                testStep.setDisabled(this.testCaseParametersMap.get(key) == null || Boolean.TRUE.equals(testStep.getDisabled()));
            }
        }
        return str;
    }

    private String replaceGlobalParameters(String str, TestStep testStep) {
        for(TestProjectGlobalParametersRequest globalParams : projectRequest.getProjectParameters()){
            if(str.contains("[[" + globalParams.getName() + "]]")) {
                str = str.replace("[[" + globalParams.getName() + "]]", ObjectUtils.defaultIfNull(globalParams.getValue(), ""));
                testStep.setDisabled(globalParams.getValue() == null || Boolean.TRUE.equals(testStep.getDisabled()));
            }
        }
        return str;
    }


    private TestProjectElementRequest findElementByIdInProject(String elementId){
        return projectRequest.getElements().stream().filter(elementRequest ->
                elementRequest.getId().equals(elementId)).findFirst().orElse(null);
    }

    private void setDisabled(TestProjectTestStepRequest stepRequest, TestStep testStep, boolean nlpMappingNotFound){
        boolean isDisabled = !stepRequest.getEnabled() || nlpMappingNotFound;
        testStep.setDisabled(isDisabled);
    }

    private void setIgnoreAndStepPriority(TestProjectTestStepRequest stepRequest, TestStep testStep){
        Boolean isIgnoreStep;
        if(stepRequest.getSettings().getFailureBehaviorType().equals("Inherit")){
            isIgnoreStep = testCaseRequest.getSettings().isIgnoreStep();
        } else {
            isIgnoreStep = stepRequest.getSettings().isIgnoreStep();
        }
        TestStepPriority priority = isIgnoreStep ? TestStepPriority.MINOR : TestStepPriority.MAJOR;
        testStep.setPriority(priority);
        testStep.setIgnoreStepResult(isIgnoreStep);

    }

    private void setStepTimeOut(TestProjectTestStepRequest stepRequest, TestStep testStep){
        Long stepTimeOut = stepRequest.getSettings().getTimeout();
        if(stepTimeOut == -1){
            stepTimeOut = testCaseRequest.getSettings().getStepTimeout();
        }
        Integer stepTimeOutInSeconds = stepTimeOut.intValue() /1000;
        testStep.setWaitTime(stepTimeOutInSeconds);
    }

}
