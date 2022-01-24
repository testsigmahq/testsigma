/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import org.springframework.util.Base64Utils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class AzureService {

  private final HttpClient httpClient;
  private final ApplicationConfig config;

  @Getter
  @Setter
  private Integrations applicationConfig;

  public TestCaseResultExternalMapping addIssue(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ArrayNode payloads = jnf.arrayNode();
    ObjectNode payload = jnf.objectNode();
    payload.put("op", "add");
    payload.put("path", "/fields/System.Title");
    payload.put("value", mapping.getFields().get("title").toString());
    payloads.add(payload);
    payload = jnf.objectNode();
    payload.put("op", "add");
    payload.put("path", "/fields/System.Description");
    payload.put("value", mapping.getFields().get("description").toString());
    payloads.add(payload);
    payload = jnf.objectNode();
    payload.put("op", "add");
    payload.put("path", "/fields/System.AreaPath");
    payload.put("value", mapping.getFields().get("project").toString());
    payloads.add(payload);
    HttpResponse<JsonNode> response = httpClient.post(applicationConfig.getUrl() + "/" + mapping.getFields().get("project").toString().replaceAll(" ", "%20")
      + "/_apis/wit/workitems/$" + mapping.getFields().get("issue_type_id").toString().replaceAll(" ", "%20") + "?api-version=6.0", getHeaders(true), payloads, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Azure issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(response.getResponseEntity().get("id").asText());
    mapping.setMisc(response.getResponseText());
    return mapping;
  }

  public void unlink(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    ArrayNode payloads = jnf.arrayNode();
    payload.put("op", "add");
    payload.put("path", "/fields/System.History");
    payload.put("value", "Unlinked from testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    payloads.add(payload);
    addHistory(mapping, payloads);
  }


  public Map<String, Object> fetchIssue(TestCaseResultExternalMapping mapping) throws TestsigmaException {

    HttpResponse<Map<String, Object>> response = httpClient.get(applicationConfig.getUrl()
        + "/_apis/wit/workitems?ids=" + mapping.getExternalId() + "&fields=System.Id,System.Title,System.WorkItemType,System.Description,System.CreatedDate,System.AssignedTo,System.State,System.AreaPath,System.ChangedDate",
      getHeaders(false), new TypeReference<Map<String, Object>>() {
      });
    return response.getResponseEntity();
  }


  public TestCaseResultExternalMapping link(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    ArrayNode payloads = jnf.arrayNode();
    payload.put("op", "add");
    payload.put("path", "/fields/System.History");
    payload.put("value", "Linked to testsigma results [" + config.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    payloads.add(payload);
    addHistory(mapping, payloads);
    return mapping;
  }

  private void addHistory(TestCaseResultExternalMapping mapping, ArrayNode payloads) throws TestsigmaException {
    Header add = new BasicHeader("X-HTTP-Method-Override", "PATCH");
    List<Header> override = getHeaders(true);
    override.add(add);
    String url = applicationConfig.getUrl() + "/_apis/wit/workitems/" + mapping.getExternalId() + "?api-version=6.0";
    HttpResponse<JsonNode> response = httpClient.post(url, override, payloads, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking Azure issue with ::" + mapping.getFields());
    }
  }

  public JsonNode getIssuesList(String project, String issueType, String title) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    String payload_input = "Select [System.Id],[System.Title],[System.WorkItemType],[System.Description],[System.CreatedDate]," +
      "[System.AssignedTo],[System.State],[System.AreaPath],[System.ChangedDate] From WorkItems Where [System.WorkItemType] ='" +
      issueType + "' AND [System.AreaPath]= '" + project + "'";
    if (title != null)
      payload_input += " AND [System.Title] Contains '" + title + "'";
    payload.put("query", payload_input);
    HttpResponse<JsonNode> response = httpClient.post(applicationConfig.getUrl() + "/" + project.replaceAll(" ", "%20") + "/_apis/wit/wiql?api-version=6.0",
      getHeaders(false), payload, new TypeReference<JsonNode>() {
      });
    return response.getResponseEntity();
  }

  public JsonNode fetchIssuesData(String idsString) throws TestsigmaException {

    HttpResponse<JsonNode> response = httpClient.get(applicationConfig.getUrl() + "/_apis/wit/workitems?ids=" + idsString + "&fields=System.Id,System.Title,System.WorkItemType,System.Description,System.State,System.AreaPath",
      getHeaders(false), new TypeReference<JsonNode>() {
      });
    return response.getResponseEntity();
  }

  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(applicationConfig.getUrl() + "/_apis/projects", getHeaders(false), new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while fetching the projects ");
    }
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " +
      Base64Utils.encodeToString(String.format("%s:%s", "", testAuth.getToken()).getBytes()));
    List<Header> headers = Lists.newArrayList(contentType, authentication);

    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() + "/_apis/projects", headers, new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  public JsonNode issueTypes(String project) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(applicationConfig.getUrl() + "/" + project.replaceAll(" ", " %20") + "/_apis/wit/workitemtypecategories", getHeaders(false), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  private List<Header> getHeaders(boolean isPatchType) {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    if (isPatchType) {
      contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json-patch+json");
    }
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " +
      Base64Utils.encodeToString(String.format("%s:%s", "", this.applicationConfig.getToken()).getBytes()));

    return Lists.newArrayList(contentType, authentication);
  }
}
