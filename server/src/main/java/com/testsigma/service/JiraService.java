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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.*;
import com.testsigma.dto.JiraProjectDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.IntegrationsRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class JiraService {
  private static final ObjectMapper om =
    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final HttpClient httpClient;
  private final StorageServiceFactory storageServiceFactory;
  private final TestStepResultService testStepResultService;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("fields", mapping.getFields());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/rest/api/2/issue", getHeaders(), payload, new TypeReference<>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating jira issue with ::" + mapping.getFields());
    }
    uploadVideo(mapping);
    uploadScreenshots(mapping);
    mapping.setExternalId(new JSONObject(response.getResponseText()).optString("key", null));
    mapping.setMisc(response.getResponseText());
    return mapping;
  }

  public void unlink(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("body", "Unlinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/rest/api/2/issue/" + mapping.getExternalId() + "/comment", getHeaders(), payload, new TypeReference<>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unlinking jira issue with ::" + mapping.getFields());
    }
  }

  public List<JiraProjectDTO> getIssueFields(String projectId, String issueType) throws TestsigmaException, EncoderException {
    String query = "?expand=projects.issuetypes.fields";
    if (projectId != null)
      query += "&projectKeys=" + new URLCodec().encode(projectId);
    if (issueType != null)
      query += "&issuetypeNames=" + new URLCodec().encode(issueType);
    HttpResponse<String> response = httpClient.get(integrations.getUrl() + "/rest/api/3/issue/createmeta" + query, getHeaders(), new TypeReference<>() {
    });

    JSONObject createMeta = new JSONObject(response.getResponseText());
    JSONArray projects = new JSONArray();
    if (createMeta.has("projects")) {
      projects = createMeta.getJSONArray("projects");
    }
    List<JiraProjectDTO> jiraProjectDTOS = new ArrayList<>();
    try {
      jiraProjectDTOS = om.readValue(String.valueOf(projects), TypeFactory.defaultInstance().constructCollectionType(List.class,
        JiraProjectDTO.class));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return jiraProjectDTOS;
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    String authHeader = HttpClient.getBasicAuthString(testAuth.getUsername() + ":" + testAuth.getPassword());
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
    List<Header> headers = Lists.newArrayList(contentType, authentication);
    String query = "?expand=projects.issuetypes.fields";
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() + "/rest/api/3/issue/createmeta" + query, headers, new TypeReference<>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  public Map<String, Object> fetchIssue(EntityExternalMapping mapping) throws TestsigmaException {
    HttpResponse<Map<String, Object>> response = httpClient.get(integrations.getUrl() + "/rest/api/2/issue/" + mapping.getExternalId() + "?expand=names,renderedFields", getHeaders(), new TypeReference<>() {
    });
    return response.getResponseEntity();
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("body", "Linked to testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + "/rest/api/2/issue/" + mapping.getExternalId() + "/comment", getHeaders(), payload, new TypeReference<>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_CREATED) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking jira issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode getIssuesList(String projectId, String issueType, String summary) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    {
      String jqlString = "project = " + projectId;
      if (issueType != null) {
        jqlString += " AND issuetype = " + issueType;
      }
      if (summary != null) {
        jqlString += " AND summary ~ '" + summary + "*'";
      }
      payload.put("jql", jqlString);
      ArrayNode fields = payload.putArray("fields");
      {
        fields.add("id");
        fields.add("key");
        fields.add("summary");
        fields.add("description");
        fields.add("status");
      }
    }
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/rest/api/2/search", getHeaders(), payload, new TypeReference<>() {
    });
    return response.getResponseEntity();
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    String authHeader = HttpClient.getBasicAuthString(this.integrations.getUsername() + ":" + this.integrations.getPassword());
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
    return Lists.newArrayList(contentType, authentication);
  }

  private List<Header> getUploadHeaders() {
    Header contentType = new BasicHeader("X-Atlassian-Token", "nocheck");
    String authHeader = HttpClient.getBasicAuthString(this.integrations.getUsername() + ":" + this.integrations.getPassword());
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
    return Lists.newArrayList(contentType, authentication);
  }

  public void uploadVideo(EntityExternalMapping mapping) {
    (new Thread(() -> {

      try {
        String videoFullPath = "/executions/videos/" + mapping.getTestCaseResult().getEnvironmentResultId() + "/video.mp4";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        Optional<URL> url = storageServiceFactory.getStorageService().generatePreSignedURLIfExists(videoFullPath, StorageAccessLevel.READ);
        if (url != null && url.isPresent()) {
          httpClient.post(integrations.getUrl() + "/rest/api/2/issue/" + mapping.getExternalId() + "/attachments", getUploadHeaders(), "video.mp4", url.get().openStream(), new TypeReference<Map<String, Object>>() {
          }, null);
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        log.error("Problem while uploading video as attachment to jira issue::" + mapping.getExternalId());
      }
    })).start();
  }

  private void uploadScreenshots(EntityExternalMapping mapping) {
    List<TestStepResult> stepResults = testStepResultService.findAllByTestCaseResultIdAndScreenshotNameIsNotNull(mapping.getTestCaseResult().getId());
    (new Thread(() -> {
      try {
        for (TestStepResult stepResult : stepResults) {
          String fileFullPath =
            "/executions/" + mapping.getTestCaseResult().getId() + "/" + stepResult.getScreenshotName();
          Calendar cal = Calendar.getInstance();
          cal.add(Calendar.MINUTE, 30);
          Optional<URL> url = storageServiceFactory.getStorageService().generatePreSignedURLIfExists(fileFullPath, StorageAccessLevel.READ);
          if (url != null && url.isPresent()) {
            httpClient.post(integrations.getUrl() + "/rest/api/2/issue/" + mapping.getExternalId() + "/attachments", getUploadHeaders(), stepResult.getScreenshotName(), url.get().openStream(), new TypeReference<Map<String, Object>>() {
            }, null);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        log.error("Problem while uploading video as attachment to jira issue::" + mapping.getExternalId());
      }
    })).start();
  }
}
