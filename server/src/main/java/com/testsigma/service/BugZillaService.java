package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.EntityExternalMapping;
import com.testsigma.model.Integrations;
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
public class BugZillaService {

  private final HttpClient httpClient;
  private final ApplicationConfig config;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("summary", mapping.getFields().get("summary").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("component", mapping.getFields().get("issueType").toString());
    payload.put("version", mapping.getFields().get("version").toString());
    payload.put("product", mapping.getFields().get("project").toString());
    payload.put("op_sys", "All");
    payload.put("rep_platform", "All");
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/rest/bug?api_key=" + integrations.getToken(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating BugZilla issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(String.valueOf(response.getResponseEntity().get("id")));
    return mapping;
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("comment", "Linked to testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/rest/bug/" + mapping.getExternalId() + "/comment?api_key=" + this.integrations.getToken(), getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking BugZilla issue with ::" + mapping.getExternalId());
    }
    return mapping;
  }

  public EntityExternalMapping unlink(EntityExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("comment", "Unlinked from testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/rest/bug/" + mapping.getExternalId() + "/comment?api_key=" + this.integrations.getToken(), getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unLinking BugZilla issue with ::" + mapping.getExternalId());
    }
    return mapping;
  }

  public JsonNode getIssuesList(String project, String issueType, String version) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/rest/bug?project=" + project + "&component=" + issueType + "&version=" + version, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssue(Long issueId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/rest/bug/" + issueId, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  //projects
  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/rest/product_accessible?api_key=" + integrations.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    JsonNode projectIds = response.getResponseEntity().get("ids");
    StringBuilder result = new StringBuilder();
    projectIds.forEach(id -> {
      result.append("ids=");
      result.append(id.toString());
      result.append("&");
    });
    result.deleteCharAt(result.length() - 1);
    response = httpClient.get(integrations.getUrl() + "/rest/product?api_key=" + integrations.getToken() + "&" + result, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {

    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl()
      + "/rest/product_accessible?api_key=" + testAuth.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    return Lists.newArrayList(contentType);
  }
}
