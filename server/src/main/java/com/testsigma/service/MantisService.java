package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class MantisService {
  private static final ObjectMapper om =
    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("summary", mapping.getFields().get("summary").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    ObjectNode projectNode = jnf.objectNode();
    projectNode.put("name", mapping.getFields().get("project").toString());
    payload.put("project", projectNode);
    ObjectNode categoryNode = jnf.objectNode();
    categoryNode.put("name", mapping.getFields().get("category").toString());
    payload.put("category", categoryNode);
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/api/rest/issues/", getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Mantis issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(String.valueOf(response.getResponseEntity().get("issue").get("id")));
    return mapping;
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    ObjectNode noteNode = jnf.objectNode();
    noteNode.put("text", "Linked to testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    ArrayList<ObjectNode> listOfNotes = new ArrayList<>();
    listOfNotes.add(noteNode);
    ArrayNode arrayNode = om.valueToTree(listOfNotes);
    payload.putArray("notes").addAll(arrayNode);
    HttpResponse<JsonNode> response = httpClient.patch(integrations.getUrl() + "/api/rest/issues/" + mapping.getExternalId(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Mantis issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public EntityExternalMapping unlink(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    ObjectNode noteNode = jnf.objectNode();
    noteNode.put("text", "Unlinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    ArrayList<ObjectNode> listOfNotes = new ArrayList<>();
    listOfNotes.add(noteNode);
    ArrayNode arrayNode = om.valueToTree(listOfNotes);
    payload.putArray("notes").addAll(arrayNode);
    HttpResponse<JsonNode> response = httpClient.patch(integrations.getUrl() + "/api/rest/issues/" + mapping.getExternalId(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Mantis issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode getIssuesList(String projectId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/rest/issues?project_id=" + projectId, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssue(Long issueId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/rest/issues/" + issueId, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/rest/projects/", getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, testAuth.getToken());
    List<Header> headers = Lists.newArrayList(contentType, authentication);
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() + "/api/rest/projects/", headers, new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, this.integrations.getToken());
    return Lists.newArrayList(contentType, authentication);
  }
}
