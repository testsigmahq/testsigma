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
public class TrelloService {
  private final HttpClient httpClient;
  private final ApplicationConfig config;

  @Getter
  @Setter
  private Integrations applicationConfig;

  public TestCaseResultExternalMapping addIssue(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("name", mapping.getFields().get("name").toString());
    payload.put("desc", mapping.getFields().get("description").toString());
    payload.put("idList", mapping.getFields().get("issueTypeId").toString());
    HttpResponse<JsonNode> response = httpClient.post("https://api.trello.com/1/cards?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Trello issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(String.valueOf(response.getResponseEntity().get("id")).replace("\"", ""));
    return mapping;
  }

  public TestCaseResultExternalMapping link(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("text", "Linked to testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<JsonNode> response = httpClient.post("https://api.trello.com/1/cards/" + mapping.getExternalId() + "/actions/comments?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Trello issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public TestCaseResultExternalMapping unlink(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    payload.put("text", "Unlinked from testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<JsonNode> response = httpClient.post("https://api.trello.com/1/cards/" + mapping.getExternalId() + "/actions/comments?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while UnLinking Trello issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  //card
  public JsonNode getIssue(String cardId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.trello.com/1/cards/" + cardId + "?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  //cards
  public JsonNode getIssuesList(String listId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.trello.com/1/lists/" + listId + "/cards?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  //lists
  public JsonNode getIssueTypes(String boardId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.trello.com/1/boards/" + boardId + "/lists?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  //boards
  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.trello.com/1/members/me/boards?key=" + applicationConfig.getPassword() + "&token=" + applicationConfig.getToken(), getHeaders(), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if (response.getStatusCode() == 401) {
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.trello.com/1/members/me/boards?key=" + testAuth.getPassword() + "&token=" + testAuth.getToken(), getHeaders(), new TypeReference<JsonNode>() {
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
