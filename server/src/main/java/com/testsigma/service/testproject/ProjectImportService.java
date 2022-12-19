package com.testsigma.service.testproject;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.web.request.testproject.TestProjectApplicationRequest;
import com.testsigma.web.request.testproject.TestProjectGlobalParametersRequest;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class ProjectImportService extends BaseImportService<TestProjectYamlRequest> {

    private final EntityExternalMappingService entityExternalMappingService;
    private final TestCaseImportService testCaseImportService;
    private final WorkspaceVersionService workspaceVersionService;
    private final EnvironmentService environmentService;
    private final IntegrationsService integrationService;
    private final WorkspaceService workspaceService;

    private WorkspaceVersion workspaceVersion;
    private Integrations integration;

    private static final String DEFAULT_TESTPROJECT_DESCRIPTION = "Created from TestProject import";

    public void importFromRequest(TestProjectYamlRequest projectRequest) throws ResourceNotFoundException, TestProjectImportException {
        if(projectRequest.getProjectName() == null || projectRequest.getTests() == null) {
            return;
        }
        Optional<Integrations> integrations = integrationService.findOptionalByApplication(Integration.TestProjectImport);
        if(integrations.isEmpty()) {
            return;
        }
        this.integration = integrations.get();
        List<EntityExternalMapping> optionalEntityExternalMapping = entityExternalMappingService.findByExternalIdAndEntityTypeAndApplicationId(projectRequest.getProjectName(), EntityType.WORKSPACE_VERSION, this.integration.getId());
        if(optionalEntityExternalMapping.isEmpty()) {
            this.workspaceVersion = createWorkspaceVersion(projectRequest);
            createEntityExternalMappingIfNotExists(projectRequest.getProjectName(), EntityType.WORKSPACE_VERSION, this.workspaceVersion.getId(), this.integration);
        } else {
            this.workspaceVersion = optionalEntityExternalMapping.get(0).getWorkspaceVersion();
        }

        createOrAppendEnvironmentParams(projectRequest);
        testCaseImportService.importFromRequest(projectRequest, this.workspaceVersion, this.integration);
    }

    private void createOrAppendEnvironmentParams(TestProjectYamlRequest projectRequest) {
        if(!projectRequest.getProjectParameters().isEmpty()){
            return;
        }
        for(TestProjectGlobalParametersRequest testProjectGlobalParametersRequest : projectRequest.getProjectParameters()) {
            Optional<Environment> environment = environmentService.findByName(testProjectGlobalParametersRequest.getName());
            if(environment.isEmpty()) {
                Environment newEnvironment = new Environment();
                newEnvironment.setName(projectRequest.getProjectName());
                Map<String, String> keyPair = new HashMap<>();
                keyPair.put(testProjectGlobalParametersRequest.getName(), testProjectGlobalParametersRequest.getValue());
                newEnvironment.setData(keyPair);
                environmentService.create(newEnvironment);
            } else {
                Environment newEnvironment = environment.get();
                Map<String, String> keyPair = newEnvironment.getData();
                keyPair.put(testProjectGlobalParametersRequest.getName(), testProjectGlobalParametersRequest.getValue());
                newEnvironment.setData(keyPair);
                environmentService.update(newEnvironment);
            }
        }
    }

    private WorkspaceVersion createWorkspaceVersion(TestProjectYamlRequest projectRequest){
        Workspace workspace = workspaceService.findByTypeAndIsDemo(projectRequest.getTests().get(0).getApplication().getPlatform(), false);
        Optional<WorkspaceVersion> optionalWorkspaceVersion = workspaceVersionService.findByWorkspaceIdAndVersionName(workspace.getId(), projectRequest.getProjectName());
        if(optionalWorkspaceVersion.isPresent()) {
            return optionalWorkspaceVersion.get();
        }
        WorkspaceVersion workspaceVersion = new WorkspaceVersion();
        workspaceVersion.setWorkspaceId(workspace.getId());
        workspaceVersion.setVersionName(projectRequest.getProjectName());
        workspaceVersion.setDescription(DEFAULT_TESTPROJECT_DESCRIPTION);
        return workspaceVersionService.create(workspaceVersion);
    }

}
