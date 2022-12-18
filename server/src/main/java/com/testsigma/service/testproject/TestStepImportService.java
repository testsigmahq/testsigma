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

    private TestProjectYamlRequest projectRequest;
    private TestProjectTestCaseRequest testCaseRequest;
    private WorkspaceVersion workspaceVersion;
    private Integrations integration;
    private Map<String, String> globalParams;

    //TODO: Revisit this when we have multiple test data support
    public void importSteps(TestProjectYamlRequest projectRequest,
                            TestCase testCase,
                            TestProjectTestCaseRequest testCaseRequest,
                            WorkspaceVersion workspaceVersion,
                            Integrations integration) throws ResourceNotFoundException, TestProjectImportException {
        this.projectRequest = projectRequest;
        this.testCaseRequest = testCaseRequest;
        this.workspaceVersion = workspaceVersion;
        this.integration = integration;
        this.globalParams = new HashMap<>();
        int stepPosition = 0;
        for(TestProjectTestStepRequest stepRequest : testCaseRequest.getSteps()){
            importStepIntoTestCase(stepRequest, testCase, stepPosition++);
        }
    }

    private void importStepIntoTestCase(TestProjectTestStepRequest stepRequest,
                                        TestCase testCase, Integer initialStepId) throws TestProjectImportException, ResourceNotFoundException {
        createTestStep(stepRequest, testCase, initialStepId);
    }



    private void createTestStep(TestProjectTestStepRequest stepRequest,
                                TestCase testCase, Integer position) throws TestProjectImportException, ResourceNotFoundException {
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
        testStep.setTestCaseId(testCase.getId());
        setStepTimeOut(stepRequest, testStep);
        setIgnoreAndStepPriority(stepRequest, testStep);
        testStep.setType(stepRequest.getStepType());
        setTestDataIfExists(stepRequest, testStep);
        setElementIfExists(stepRequest,testCase.getWorkspaceVersionId(), testStep);
        testStep.setPosition(position);
        if(stepRequest.getStepType() == TestStepType.ACTION_TEXT){
            if(stepRequest.getAction() != null){
                String action = testProjectNLPs.getNlpByIdAndType(stepRequest.getAction().getId(), this.workspaceVersion.getWorkspace().getWorkspaceType()).getDescription();
                String replacedAction = replaceActionWithParams(action, stepRequest, testStep);
                testStep.setAction(replacedAction);
            }
            else
                log.info("Action not found for step id - " + stepRequest.getId());
        }
        setDisabled(stepRequest, testStep, nlpMappingNotFound);
        testStepService.save(testStep);
    }

    private void populateParams() {
        for(TestProjectGlobalParametersRequest parameter : this.projectRequest.getProjectParameters()) {
            this.globalParams.put(parameter.getName(), parameter.getValue());
        }
        this.projectRequest.getTests().forEach(test -> test.getParameters().forEach(stepParam -> {{
            this.globalParams.put(stepParam.getName(), stepParam.getValue());
        }}));
    }

    private String replaceActionWithParams(String action, TestProjectTestStepRequest stepRequest, TestStep testStep) throws ResourceNotFoundException, TestProjectImportException {
        List<TestProjectStepParameter> parameters = stepRequest.getParameterMaps();
        for(TestProjectStepParameter parameter : parameters){
            String value = parameter.getValue();
            if(parameter.getValue().startsWith("{{")) {
                value = parameter.getValue().substring(2, parameter.getValue().length() - 2);
                if (this.globalParams.containsKey(value)) {
                    action = action.replace("{{" + parameter.getName() + "}}", this.globalParams.get(value));
                }
            } else {
                action = action.replace("{{" + parameter.getName() + "}}", value);
            }
        }
        for(TestProjectGlobalParametersRequest globalParams : projectRequest.getProjectParameters()){
            String value = globalParams.getValue();
            if(globalParams.getValue().startsWith("[[")) {
                value = globalParams.getValue().substring(2, globalParams.getValue().length() - 2);
                if (this.globalParams.containsKey(value)) {
                    action = action.replace("[[" + globalParams.getName() + "]]", this.globalParams.get(value));
                }
            } else {
                action = action.replace("[[" + globalParams.getName() + "]]", ObjectUtils.defaultIfNull(value,""));
            }
        }
        if(stepRequest.getElementId() != null){
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
                        ExternalImportType.TEST_PROJECT, workspaceVersion.getWorkspace().getWorkspaceType());
        if (nlpMapping.isPresent()) {
            return nlpMapping.get().getTestsigmaNlpId();
        }
        return 0;
    }

    private void setTestDataIfExists(TestProjectTestStepRequest stepRequest, TestStep testStep){
        testStep.setTestDataType(TestDataType.raw.name());
        if(!stepRequest.getParameterMaps().isEmpty()) {
            String stepTestData = stepRequest.getParameterMaps().get(0).getValue();
            if(stepTestData.startsWith("{{") || stepTestData.startsWith("[[")){
                stepTestData = stepTestData.substring(2,stepTestData.length()-2);
                if(this.globalParams.containsKey(stepTestData)) {
                    stepTestData = this.globalParams.get(stepTestData);
                }
                testStep.setTestDataType(TestDataType.raw.name());
            }
            testStep.setTestData(stepTestData);
        }
    }

    private void setElementIfExists(TestProjectTestStepRequest stepRequest, Long applicationVersionId,  TestStep testStep) throws ResourceNotFoundException {
        String elementId = stepRequest.getElementId();
        if(elementId != null){
            TestProjectElementRequest elementRequest = findElementByIdInProject(elementId);
            Element element = elementsImportService.checkAndCreateElement(elementRequest, applicationVersionId, this.integration);
            testStep.setElement(element.getName());
        }
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
