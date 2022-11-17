package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
@Log4j2
public class XrayCloudService implements XrayService {

    private final IntegrationsService externalConfigService;
    private final EntityExternalMappingService entityMappingService;
    private final TestPlanResultService executionResultService;
    private final TestDeviceResultService environmentResultService;
    private final TestDeviceService environmentService;
    private final TestCaseResultService testCaseResultService;
    private final TestSuiteResultService testSuiteResultService;
    private final TestSuiteService testSuiteService;
    private final TestDataSetService testDataSetService;
    private final HttpClient httpClient;
    private final JiraService jiraService;
    private final PlatformsService platformsService;
    public final static String XRAY_CLOUD_URL = "https://xray.cloud.getxray.app/api/v2/";
    public final static String XRAY_CLOUD_AUTHENTICATE = XRAY_CLOUD_URL + "authenticate";
    public final static String XRAY_CLOUD_IMPORT_EXECUTION = XRAY_CLOUD_URL + "import/execution";
    public final static String XRAY_GRAPHQL = "https://xray.cloud.getxray.app/api/v2/graphql";

    public void link(EntityExternalMapping mapping) throws TestsigmaException {
        Optional<Integrations> jiraConfig = this.externalConfigService.findOptionalByApplication(Integration.Jira);
        if(jiraConfig.isPresent()){
            this.jiraService.setIntegrations(jiraConfig.get());
            Map<String, Object> issue = this.jiraService.fetchIssue(mapping);
            if(issue == null)
                throw new ResourceNotFoundException("Invalid Xray Id");
        }
    }

    @Override
    public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
        XrayClientRequest request = new XrayClientRequest();
        request.setClientId(testAuth.getUsername());
        request.setClientSecret(testAuth.getPassword());
        HttpResponse<String> response = this.authenticate(request);
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode status = jnf.objectNode();
        status.put("status_code", response.getStatusCode());
        status.put("status_message", response.getStatusMessage());
        String token = getValue(response.getResponseEntity());
        status.put("api_token", token);
        return status;
    }

    private HttpResponse<String> authenticate(XrayClientRequest request) throws TestsigmaException {
        return httpClient.post(XRAY_CLOUD_AUTHENTICATE, getHeaders(null), request, new TypeReference<>() {

        });
    }

    public Integrations reGenerateApiToken() throws TestsigmaException {
        Integrations integrations = this.externalConfigService.findOptionalByApplication(Integration.XrayCloud).get();
        XrayClientRequest request = new XrayClientRequest();
        request.setClientId(integrations.getUsername());
        request.setClientSecret(integrations.getPassword());
        HttpResponse<String> response = this.authenticate(request);
        String token = getValue(response.getResponseEntity());
        integrations.setToken(token);
        integrations = this.externalConfigService.save(integrations);
        return integrations;
    }

    public void exportResultsByEnvironmentResultId(Long environmentResultId) {
        List<TestSuiteResult> testSuiteResults = this.testSuiteResultService.findAllByEnvironmentResultId(environmentResultId);
        for (TestSuiteResult suiteResult : testSuiteResults) {
            try {
                if(this.beforeExportCheck(suiteResult.getTestPlanResultId())){
                    pushBySuiteResultId(suiteResult.getId());
                }
            } catch (Exception e) {
                log.info("Xray Results Push Job Initialization Failed for Suite Result Id : " + suiteResult.getId());
                log.error(e.getMessage(), e);
            }
        }
    }

    public void exportResultsBySuiteResult(TestSuiteResult testSuiteResult) {
        try {
            if(this.beforeExportCheck(testSuiteResult.getTestPlanResultId())) {
                pushBySuiteResultId(testSuiteResult.getId());
            }
        } catch (Exception e) {
            log.info("Starting an Xray Results Push Job Failed for Suite Result Id : " + testSuiteResult.getId());
            log.error(e.getMessage(), e);
        }
    }

    private Boolean beforeExportCheck(Long executionResultId) throws ResourceNotFoundException {
        Optional<Integrations> xrayConfig = this.externalConfigService.findOptionalByApplication(Integration.XrayCloud);
        if(xrayConfig.isPresent()) {
            TestPlanResult executionResult = this.executionResultService.find(executionResultId);
            if (!executionResult.getTestPlan().getEntityType().equals("ADHOC_EXECUTION")) {
                return true;
            }
        }
        return false;
    }

    public void pushBySuiteResultId(Long suiteResultId) {
        Optional<Integrations> xrayConfig = this.externalConfigService.findOptionalByApplication(Integration.XrayCloud);
        if(xrayConfig.isPresent()) {
            try {
                TestSuiteResult testSuiteResult = this.testSuiteResultService.find(suiteResultId);
                TestSuiteResult parentResult = this.testSuiteResultService.getFirstParentResult(suiteResultId);
                Optional<EntityExternalMapping> mappingOptional = this.entityMappingService.findByEntityIdAndEntityType(parentResult.getId(),
                        EntityType.TEST_SUITE_RESULT, xrayConfig.get().getId());

                EntityExternalMapping mapping = new EntityExternalMapping();
                if (mappingOptional.isPresent())
                    mapping = mappingOptional.get();
                else {
                    mapping.setEntityId(testSuiteResult.getId());
                    mapping.setApplicationId(xrayConfig.get().getId());
                    mapping.setEntityType(EntityType.TEST_SUITE_RESULT);
                }
                XrayCloudRequest payload = createResultsPayload(testSuiteResult, mapping);

                if (payload != null) {
                    log.info("Pushing the results for the test suite id :" + testSuiteResult.getId() + " With payload: " + payload);
                    this.postRequestToXray(payload, mapping, xrayConfig.get(), false);
                }
            } catch (ResourceNotFoundException e) {
                log.info("Failed to push the results to Xray for suite result Id: " + suiteResultId);
                log.error(e.getMessage(), e);
            } catch (TestsigmaException e) {
                log.info("Failed to push the results to Xray for suite result Id: " + suiteResultId);
                log.error(e.getMessage(), e);
            }
        }
    }

    private void postRequestToXray(XrayCloudRequest payload, EntityExternalMapping mapping,
                                   Integrations xrayConfig,
                                   Boolean reTry) throws TestsigmaException {
        if (reTry)
            xrayConfig = this.reGenerateApiToken();
        HttpResponse<XrayResponse> response = httpClient.post(XRAY_CLOUD_IMPORT_EXECUTION, getHeaders(xrayConfig.getToken()),
                payload, new TypeReference<>() {
                });
        if (response.getStatusCode() == 200) {
            saveSuccessResponseStatus(response.getResponseEntity(), mapping);
            this.pushEnvironmentsToXray(response.getResponseEntity(), xrayConfig, mapping);
        } else if (response.getStatusCode() == 401 && !reTry) {
            log.info("Results push to Xray is failed attempting the re-generation of token");
            postRequestToXray(payload, mapping, xrayConfig, true);
        } else {
            log.info("Results push to Xray is failed for payload : " + payload + " Marking it as failed to sync");
            saveFailedResponseStatus(response, mapping);
        }
    }

    private void saveFailedResponseStatus(HttpResponse<XrayResponse> response, EntityExternalMapping mapping) {
        mapping.setPushFailed(Boolean.TRUE);
        mapping.setMessage(response.getResponseText());
        this.entityMappingService.save(mapping);
    }

    private void saveSuccessResponseStatus(XrayResponse response, EntityExternalMapping mapping) throws IntegrationNotFoundException {
        if (mapping.getId() == null) {
            mapping.setExternalId(response.getKey());
        }
        mapping.setPushFailed(Boolean.FALSE);
        mapping.setMessage("Results Pushed to Xray Successfully");
        this.entityMappingService.save(mapping);
    }

    private void pushEnvironmentsToXray(XrayResponse response,
                                        Integrations xrayConfig,
                                        EntityExternalMapping mapping) throws TestsigmaException {
        List<String> environments = populateEnvironments(mapping.getEntityId());
        if(!environments.isEmpty()) {
            XrayGraphQLVariables variables = new XrayGraphQLVariables();
            variables.setIssueId(response.getId());
            variables.setEnvironments(environments);
            try {
                final String query = GraphqlSchemaReaderService.getSchemaFromFileName("XrayTestEnvironments");
                GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
                graphQLRequestBody.setQuery(query);
                graphQLRequestBody.setVariables(variables);
                HttpResponse<ObjectNode> response1 = this.httpClient.post(XRAY_GRAPHQL, getHeaders(xrayConfig.getToken()),
                        graphQLRequestBody, new TypeReference<>() {
                        });
                if (response1.getStatusCode() == 200)
                    log.info("Environments pushed successfully for suite result Id" + mapping.getEntityId());
                else
                    log.info("Failed to push the environments to Xray for suite result Id" + mapping.getEntityId());
            } catch (IOException e) {
                log.error("Failed to read the XrayTestEnvironments GraphQL file :" + e.getMessage());
            }
        }
    }

    private List<String> populateEnvironments(Long suiteResultId) throws TestsigmaException {
        List<String> environments = new ArrayList<>();
        TestSuiteResult suiteResult = this.testSuiteResultService.find(suiteResultId);
        TestDeviceResult environmentResult = this.environmentResultService.find(suiteResult.getEnvironmentResultId());
        TestDevice environment = this.environmentService.find(environmentResult.getTestDeviceId());
        if(environment.getTestPlanLabType() == TestPlanLabType.Hybrid){
            Platform platform = environment.getPlatform();
            Agent agent = environment.getAgent();
            environments.add(platform + "(" +platform.getVersionPrefix()+ ")");
            for (AgentBrowser agentBrowser : agent.getBrowserList()) {
                if (Objects.equals(environment.getBrowser(), agentBrowser.getName().getHybridName())) {
                    environments.add(agentBrowser.getName() + "("+agentBrowser.getMajorVersion()+")");
                    break;
                }
            }
        } else {
            PlatformOsVersion platformOsVersion = this.platformsService.getPlatformOsVersion(environment.getPlatformOsVersionId(), environment.getTestPlanLabType());
            PlatformBrowserVersion platformBrowserVersion = this.platformsService.getPlatformBrowserVersion(environment.getPlatformBrowserVersionId(), environment.getTestPlanLabType());
            environments.add(platformOsVersion.getPlatform() + "(" +platformOsVersion.getVersion() + ")");
            environments.add(platformBrowserVersion.getName() + "("+ platformBrowserVersion.getVersion()+")");
        }
        return environments;
    }

    private XrayCloudRequest createResultsPayload(TestSuiteResult testSuiteResult, EntityExternalMapping entityMapping) throws ResourceNotFoundException {
        XrayCloudRequest cloudRequest = new XrayCloudRequest();
        XrayInfoRequest info = new XrayInfoRequest();
        TestPlanResult executionResult = this.executionResultService.find(testSuiteResult.getTestPlanResultId());
        if (entityMapping.getExternalId() != null) {
            cloudRequest.setTestExecutionKey(entityMapping.getExternalId());
        }

        Optional<EntityExternalMapping> externalMapping = this.entityMappingService.findByEntityIdAndEntityType(executionResult.getTestPlanId(),
                EntityType.TEST_PLAN, entityMapping.getApplicationId());

        if (externalMapping.isEmpty()) {
            return null;
        }
        info.setTestPlanKey(externalMapping.get().getExternalId());
        TestSuite testSuite = this.testSuiteService.find(testSuiteResult.getSuiteId());
        info.setSummary(testSuite.getName());
        info.setDescription("Linked to testsigma results [https://local.testsigmaos.com/ui/td/suite_results/" + testSuiteResult.getId() + "] ");

        List<XrayTestRequest> testRequests = new ArrayList<>();
        List<TestCaseResult> testCaseResults = this.testCaseResultService.findAllBySuiteResultIdAnAndParentIdNull(testSuiteResult.getId());
        for (TestCaseResult testCaseResult : testCaseResults) {
            XrayTestRequest testRequest = new XrayTestRequest();
            externalMapping = this.entityMappingService.findByEntityIdAndEntityType(testCaseResult.getTestCaseId(), EntityType.TEST_CASE,
                    entityMapping.getApplicationId());
            if (externalMapping.isPresent()) {
                testRequest.setTestKey(externalMapping.get().getExternalId());
                if (testCaseResult.getStartTime() != null) {
                    testRequest.setStart(testCaseResult.getStartTime().toInstant().toString());
                }
                if (testCaseResult.getEndTime() != null) {
                    testRequest.setFinish(testCaseResult.getStartTime().toInstant().toString());
                }
                testRequest.setStatus(ResultConstant.getXrayStatus(testCaseResult.getResult()));
                if (testCaseResult.getIsDataDriven()) {
                    List<XrayIterationRequest> xrayIterationRequests = this.populateIterationResults(testCaseResult.getId());
                    testRequest.setIterations(xrayIterationRequests);
                }
                testRequests.add(testRequest);
            } else {
                log.info("Xray Id link missing on the Test Case Id: " + testCaseResult.getTestCaseId());
            }
        }
        cloudRequest.setInfo(info);
        cloudRequest.setTests(testRequests);
        return cloudRequest;
    }

    private List<XrayIterationRequest> populateIterationResults(Long testCaseResultId) {
        List<TestCaseResult> iterationResults = this.testCaseResultService.findAllByParentId(testCaseResultId);
        List<XrayIterationRequest> xrayIterationRequests = new ArrayList<>();
        for (TestCaseResult iterationResult : iterationResults) {
            XrayIterationRequest xrayIterationRequest = new XrayIterationRequest();
            xrayIterationRequest.setName(iterationResult.getIteration());
            xrayIterationRequest.setStatus(ResultConstant.getXrayStatus(iterationResult.getResult()));
            Optional<TestDataSet> testDataSet = this.testDataSetService.findByProfileIdAndSetName(iterationResult.getTestDataId(), iterationResult.getTestDataSetName());
            List<XrayParameterRequest> parameterRequests = new ArrayList<>();
            if (testDataSet.isPresent()) {
                xrayIterationRequest.setLog("DataSet ::" + iterationResult.getTestDataSetName());
                JSONObject jsonObject = testDataSet.get().getData();
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    XrayParameterRequest parameterRequest = new XrayParameterRequest();
                    parameterRequest.setName(key);
                    parameterRequest.setValue((String) jsonObject.get(key));
                    parameterRequests.add(parameterRequest);
                }
            } else {
                log.info("Test Data Set not found with the combination of profile id :" + iterationResult.getTestDataId()
                        + " and set name : " + iterationResult.getTestDataSetName());
            }
            xrayIterationRequest.setParameters(parameterRequests);
            xrayIterationRequests.add(xrayIterationRequest);
        }
        return xrayIterationRequests;
    }

    private List<Header> getHeaders(String token) {
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        if (token != null) {
            Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return Lists.newArrayList(contentType, authentication);
        }
        return Lists.newArrayList(contentType);
    }

    private String getValue(String key) {
        Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(key);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}
