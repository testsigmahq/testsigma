package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.Integrations;
import com.testsigma.model.TestCaseResultExternalMapping;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.IntegrationsRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class BackLogService {
  private final HttpClient httpClient;

  private final ApplicationConfig config;
  @Getter
  @Setter
  private Integrations integrations;

  public TestCaseResultExternalMapping addIssue(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("summary", mapping.getFields().get("summary").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("issueTypeId", mapping.getFields().get("issueTypeId").toString());
    payload.put("priorityId", mapping.getFields().get("priorityId").toString());
    payload.put("projectId", mapping.getFields().get("projectId").toString());
    HttpResponse<JsonNode> response = httpClient.formPost(integrations.getUrl() + "/api/v2/issues?apiKey=" + integrations.getToken(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating BackLog issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(String.valueOf(response.getResponseEntity().get("id")));
    return mapping;
  }

  public TestCaseResultExternalMapping link(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("content", "Linked to testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.formPost(integrations.getUrl() + "/api/v2/issues/" + mapping.getExternalId() + "/comments?apiKey=" + this.integrations.getToken(), getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking BackLog issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public TestCaseResultExternalMapping unlink(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("content", "Unlinked from testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.formPost(integrations.getUrl() + "/api/v2/issues/" + mapping.getExternalId() + "/comments?apiKey=" + this.integrations.getToken(), getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking BackLog issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/projects?apiKey=" + this.integrations.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  public JsonNode getIssuesList(Long projectId, Long issueTypeId, Long priorityId, String keyWord) throws TestsigmaException {
    if (keyWord == null)
      keyWord = "";
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/issues?keyword=" + keyWord + "&apiKey=" + this.integrations.getToken() + "&projectId[]=" + projectId + "&issueTypeId[]=" + issueTypeId + "&priorityId[]=" + priorityId, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssue(Long issueId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/issues?apiKey=" + this.integrations.getToken() + "&id[]=" + issueId, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getPriorities() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/priorities?apiKey=" + this.integrations.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssueTypes(Long projectId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/projects/" + projectId + "/issueTypes?apiKey=" + this.integrations.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() + "/api/v2/projects?apiKey=" + testAuth.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
    return Lists.newArrayList(contentType);
  }
}
