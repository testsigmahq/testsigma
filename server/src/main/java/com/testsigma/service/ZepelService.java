package com.testsigma.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ZepelService {
  private static final ObjectMapper om =
    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    String squadId = mapping.getFields().get("projectId").toString();
    String listId = mapping.getFields().get("issueTypeId").toString();
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("title", mapping.getFields().get("title").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("type", "Bug");
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists/" + listId + "/items", getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Zepel issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(response.getResponseEntity().get("item").get("id").textValue());
    mapping.setMisc(String.valueOf(response.getResponseEntity().get("item")));
    return mapping;
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException, IOException {
    String squadId = mapping.getFields().get("projectId").toString();
    String listId = mapping.getFields().get("issueTypeId").toString();
    String itemId = mapping.getExternalId();
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("description", "Linked to testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists/" + listId + "/items/" + itemId + "/comments", getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unlinking Zepel issue with ::" + mapping.getFields());
    }
    mapping.setMisc(String.valueOf(response.getResponseEntity().get("comment")));
    return mapping;
  }

  public EntityExternalMapping unlink(EntityExternalMapping mapping) throws TestsigmaException, IOException {
    JsonFactory factory = om.getFactory();
    JsonParser parser = factory.createParser(mapping.getMisc());
    JsonNode miscObj = om.readTree(parser);
    String squadId = String.valueOf(miscObj.get("squad_id")).replace("\"", "");
    String listId = String.valueOf(miscObj.get("list_id")).replace("\"", "");
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("description", "unlinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists/" + listId + "/items/" + mapping.getExternalId() + "/comments", getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unlinking Zepel issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  //item
  public Map<String, Object> fetchIssue(EntityExternalMapping mapping) throws TestsigmaException, IOException {
    JsonFactory factory = om.getFactory();
    JsonParser parser = factory.createParser(mapping.getMisc());
    JsonNode miscObj = om.readTree(parser);
    String squadId = String.valueOf(miscObj.get("squad_id")).replace("\"", "");
    String listId = String.valueOf(miscObj.get("list_id")).replace("\"", "");
    HttpResponse<Map<String, Object>> response = httpClient.get(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists/" + listId + "/items/" + mapping.getExternalId(), getHeaders(), new TypeReference<Map<String, Object>>() {
    });
    return (Map<String, Object>) response.getResponseEntity().get("item");
  }

  //items
  public JsonNode getIssuesList(String squadId, String listId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists/" + listId + "/items", getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  //lists
  public JsonNode getIssueTypes(String squadId) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/squads/" + squadId + "/lists", getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  //squads
  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/v2/squads", getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + testAuth.getToken());
    List<Header> headers = Lists.newArrayList(contentType, authentication);
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() +
      "/api/v2/squads", headers, new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.integrations.getToken());
    return Lists.newArrayList(contentType, authentication);
  }
}
