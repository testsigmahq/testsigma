package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ClickUpService {
  private final HttpClient httpClient;

  @Getter
  @Setter
  private Integrations workspaceConfig;

  @Autowired
  private UserPreferenceService userPreferenceService;


  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    String listId = mapping.getFields().get("listId").toString();
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("name", mapping.getFields().get("title").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("status", "to do");
    payload.put("notify_all", "true");
    HttpResponse<JsonNode> response = httpClient.post("https://api.clickup.com/api/v2/list/"+listId+"/task", getHeaders(workspaceConfig.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating ClickUp issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(response.getResponseEntity().get("id").textValue());
    mapping.setMisc(String.valueOf(response.getResponseEntity()));
    return mapping;
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    HashMap<String, String> payload = new HashMap<>();
    String taskId = mapping.getExternalId();
    payload.put("notify_all", "true");
    payload.put("comment_text","Linked to testsigma results [https://app.testsigma.com/#/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<JsonNode> response = httpClient.post("https://api.clickup.com/api/v2/task/"+taskId+"/comment", getHeaders(workspaceConfig.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Trello issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public EntityExternalMapping unlink(EntityExternalMapping mapping) throws TestsigmaException {
    String comment = "UnLinked from testsigma results [https://app.testsigma.com/#/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName();
    HashMap<String, String> payload = new HashMap<>();
    String taskId = mapping.getExternalId();
    payload.put("notify_all", "true");
    payload.put("comment_text", comment);
    HttpResponse<JsonNode> response = httpClient.post("https://api.clickup.com/api/v2/task/"+taskId+"/comment", getHeaders(workspaceConfig.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Trello issue with ::" + mapping.getFields());
    }
    return mapping;
  }



  //Teams
  public JsonNode teams() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.clickup.com/api/v2/team", getHeaders(workspaceConfig.getToken()), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if(response.getStatusCode() == 401){
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }

  //Spaces
  public JsonNode spaces(String teamId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.clickup.com/api/v2/team/"+teamId+"/space?archived=false", getHeaders(workspaceConfig.getToken()), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if(response.getStatusCode() == 401){
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }

  //folders
  public JsonNode folders(String spaceId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.clickup.com/api/v2/space/"+spaceId+"/folder?archived=false", getHeaders(workspaceConfig.getToken()), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if(response.getStatusCode() == 401){
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }


  public JsonNode lists(String folderId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.clickup.com/api/v2/folder/"+folderId+"/list?archived=false", getHeaders(workspaceConfig.getToken()), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if(response.getStatusCode() == 401){
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }

  public JsonNode tasks(String listId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get("https://api.clickup.com/api/v2/list/"+listId+"/task?archived=false", getHeaders(workspaceConfig.getToken()), new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    if(response.getStatusCode() == 401){
      status.put("status_code", response.getStatusCode());
      status.put("status_message", response.getStatusMessage());
    }
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    status.put("data", response.getResponseEntity());
    return status;
  }

  public Map<String, Object> fetchIssue(EntityExternalMapping mapping) throws TestsigmaException {

    HttpResponse<Map<String, Object>> response = httpClient.get("https://api.clickup.com/api/v2/task/"+mapping.getExternalId(),
      getHeaders(workspaceConfig.getToken()), new TypeReference<Map<String, Object>>() {
      });
    return response.getResponseEntity();
  }

  public JsonNode getIssuesList(String projectId) throws TestsigmaException, URISyntaxException {
    String query = "{ project(id: \""+projectId+"\") {issues { nodes { id title identifier description  priority team { id name}  project{id name} createdAt updatedAt } } }}";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get( getHeaders(workspaceConfig.getToken()),builder, new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssue(String issueId) throws TestsigmaException, URISyntaxException {
    String query = "{ issue(id: \""+issueId.replace("\"","")+"\") {  id title identifier description priority team { id name}  project{id name} createdAt updatedAt} }";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get( getHeaders(workspaceConfig.getToken()),builder, new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get( "https://api.clickup.com/api/v2/team", getHeaders(testAuth.getToken()), new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders(String token) {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, token);
    return Lists.newArrayList(contentType, authentication);
  }
}
