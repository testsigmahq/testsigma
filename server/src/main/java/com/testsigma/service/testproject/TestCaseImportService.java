package com.testsigma.service.testproject;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.web.request.testproject.TestProjectTestCaseRequest;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCaseImportService extends BaseImportService<TestProjectTestCaseRequest> {

    private final TestCaseService testCaseService;
    private final TestStepImportService testStepImportService;
    private final TestStepService testStepService;
    private final TestCasePriorityService testCasePriorityService;
    private final TestCaseTypeService testCaseTypeService;

    private WorkspaceVersion workspaceVersion;
    private Integrations integration;

    public void importFromRequest(TestProjectYamlRequest projectRequest,
                                  WorkspaceVersion workspaceVersion,
                                  Integrations integration) throws TestProjectImportException, ResourceNotFoundException {
        this.workspaceVersion = workspaceVersion;
        this.integration = integration;
        importTestCasesAndStepGroups(projectRequest);
    }

    private void importTestCasesAndStepGroups(TestProjectYamlRequest projectRequest)
            throws TestProjectImportException, ResourceNotFoundException {
        List<TestProjectTestCaseRequest> testCaseRequests = projectRequest.getTests();
        List<TestProjectTestCaseRequest> stepGroupRequests = projectRequest.getAuxTests();
        stepGroupRequests.addAll(testCaseRequests);
        for (TestProjectTestCaseRequest testCaseRequest : stepGroupRequests) {
            Boolean isStepGroup = !testCaseRequests.contains(testCaseRequest);
            TestCase testCase = importTestCase(testCaseRequest,isStepGroup);
            testStepImportService.importSteps(projectRequest, testCase, testCaseRequest, this.workspaceVersion, this.integration, isStepGroup);
        }
    }

    private TestCase importTestCase(TestProjectTestCaseRequest testCaseRequest, Boolean isStepGroup) {
        TestCase testCase = getTestCase(testCaseRequest, isStepGroup);
        testStepService.deleteStepsByTestCaseId(testCase.getId());
        return testCase;
    }

    private TestCase getTestCase(TestProjectTestCaseRequest testCaseRequest, Boolean isStepGroup) {
        Optional<TestCase> optionalTestCase = testCaseService.findByNameAndWorkspaceVersionId(testCaseRequest.getName(), this.workspaceVersion.getId());
        if(optionalTestCase.isEmpty()){
            TestCase testCase = createTestCase(testCaseRequest, isStepGroup);
            createEntityExternalMappingIfNotExists(testCaseRequest.getId(), EntityType.TEST_CASE, testCase.getId(), this.integration);
            return testCase;
        }
        return optionalTestCase.get();
    }

    private TestCase createTestCase(TestProjectTestCaseRequest testCaseRequest, Boolean isStepGroup) {
        TestCasePriority testCasePriority = testCasePriorityService.findByWorkspaceId(this.workspaceVersion.getWorkspaceId()).get(0);
        TestCaseType testCaseType = testCaseTypeService.findByWorkspaceId(this.workspaceVersion.getWorkspaceId()).get(0);
        TestCase testCase = new TestCase();
        testCase.setIsStepGroup(isStepGroup);
        testCase.setWorkspaceVersionId(this.workspaceVersion.getId());
        testCase.setName(testCaseRequest.getName());
        testCase.setIsDataDriven(false);
        testCase.setStatus(TestCaseStatus.READY);
        testCase.setIsActive(true);
        testCase.setPriority(testCasePriority.getId());
        testCase.setType(testCaseType.getId());
        testCase.setDeleted(false);
        testCase.setTestDataStartIndex(0);
        return testCaseService.save(testCase);
    }

}
