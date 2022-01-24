/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.testsigma.dto.IntegrationsDTO;
import com.testsigma.dto.JiraProjectDTO;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.IntegrationsMapper;
import com.testsigma.model.Integrations;
import com.testsigma.service.*;
import com.testsigma.web.request.IntegrationsRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(path = "/settings/integrations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IntegrationsController {


  private final IntegrationsService integrationsService;
  private final JiraService jiraService;
  private final FreshreleaseService freshreleaseService;
  private final MantisService mantisService;
  private final AzureService azureService;
  private final BackLogService backLogService;
  private final ZepelService zepelService;
  private final YoutrackService youtrackService;
  private final BugZillaService bugZillaService;
  private final TrelloService trelloService;
  private final LinearService linearService;
  private final IntegrationsMapper mapper;
  private final ClickUpService clickUpService;

  @RequestMapping(method = RequestMethod.POST)
  public IntegrationsDTO create(
    @RequestBody IntegrationsRequest integrationsRequest, HttpServletRequest request)
    throws TestsigmaDatabaseException {

    Integrations config = integrationsService.create(integrationsRequest);
    return mapper.map(config);
  }

  @RequestMapping(path = "/{configId}", method = RequestMethod.PUT)
  public IntegrationsDTO update(
    @RequestBody IntegrationsRequest integrationsRequest,
    @PathVariable("configId") Long configId)
    throws IntegrationNotFoundException, TestsigmaDatabaseException {
    Integrations config = integrationsService.update(integrationsRequest,
      configId);
    return mapper.map(config);
  }

  @RequestMapping(path = "/{configId}", method = RequestMethod.GET)
  public IntegrationsDTO get(@PathVariable("configId") Long configId)
    throws IntegrationNotFoundException {
    Integrations config = integrationsService.find(configId);
    return mapper.map(config);
  }

  @RequestMapping(path = "/{configId}", method = RequestMethod.DELETE)
  public HttpStatus destroy(@PathVariable("configId") Long configId)
    throws IntegrationNotFoundException {
    integrationsService.destroy(configId);
    return HttpStatus.OK;
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<IntegrationsDTO> index() {
    List<Integrations> configs = integrationsService.findAll();
    return mapper.map(configs);
  }


  @GetMapping(path = "/{id}/jira_projects")
  public List<JiraProjectDTO> jiraFields(@PathVariable("id") Long id, @Nullable @RequestParam("projectId") String projectId, @Nullable @RequestParam("issueType") String issueType) throws TestsigmaException, EncoderException {
    Integrations applicationConfig = this.integrationsService.find(id);
    this.jiraService.setIntegrations(applicationConfig);
    return this.jiraService.getIssueFields(projectId, issueType);
  }

  @GetMapping(path = "/{id}/search_jira_issues")
  public JsonNode searchIssues(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project, @NotNull @RequestParam("issueType") String issueType, @Nullable @RequestParam("summary") String summary) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    this.jiraService.setIntegrations(applicationConfig);
    return this.jiraService.getIssuesList(project, issueType, summary);
  }

  @GetMapping(path = "/{id}/freshrelease_projects")
  public JsonNode fetchFRProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    freshreleaseService.setIntegrations(applicationConfig);
    return freshreleaseService.projects();
  }

  @GetMapping(path = "/{id}/freshrelease_issue_types")
  public JsonNode fetchFRProjectIssueTypes(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    freshreleaseService.setIntegrations(applicationConfig);
    return freshreleaseService.issueTypes(project);
  }

  @GetMapping(path = "/{id}/search_freshrelease_issues")
  public JsonNode searchFreshReleaseIssues(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project, @Nullable @RequestParam("title") String title) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    this.jiraService.setIntegrations(applicationConfig);
    return this.freshreleaseService.getIssuesList(project, title);
  }

  @GetMapping(path = "/{id}/mantis_projects")
  public JsonNode fetchMantisProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    mantisService.setIntegrations(applicationConfig);
    return mantisService.projects();
  }

  @GetMapping(path = "/{id}/search_mantis_issues")
  public JsonNode fetchMantisIssues(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    mantisService.setIntegrations(applicationConfig);
    return mantisService.getIssuesList(project);
  }

  @GetMapping(path = "/{id}/search_mantis_issue/{issueId}")
  public JsonNode fetchMantisIssue(@PathVariable("id") Long id, @PathVariable("issueId") Long issueId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    mantisService.setIntegrations(applicationConfig);
    return mantisService.getIssue(issueId);
  }

  @GetMapping(path = "/{id}/azure_projects")
  public JsonNode fetchAzureProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    azureService.setApplicationConfig(applicationConfig);
    return azureService.projects();
  }

  @GetMapping(path = "/{id}/azure_issue_types")
  public JsonNode fetchAzureProjectIssueTypes(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project) throws TestsigmaException, EncoderException {
    Integrations applicationConfig = this.integrationsService.find(id);
    azureService.setApplicationConfig(applicationConfig);
    return azureService.issueTypes(project);
  }

  @GetMapping(path = "/{id}/search_azure_issues")
  public JsonNode searchAzureIssues(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project,
                                    @NotNull @RequestParam("issueType") String issueType, @Nullable @RequestParam("title") String title) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    this.azureService.setApplicationConfig(applicationConfig);
    return this.azureService.getIssuesList(project, issueType, title);
  }

  @GetMapping(path = "/{id}/get_azure_issues")
  public JsonNode getAzureIssuesData(@PathVariable("id") Long id, @NotNull @RequestParam("ids") String issueIds) throws TestsigmaException {
    return this.azureService.fetchIssuesData(issueIds);
  }

  @GetMapping(path = "/{id}/backlog_projects")
  public JsonNode fetchBackLogProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    backLogService.setIntegrations(applicationConfig);
    return backLogService.projects();
  }

  @GetMapping(path = "/{id}/search_backlog_issue_types")
  public JsonNode fetchBackLogIssueTypes(@PathVariable("id") Long id, @NotNull @RequestParam("project") Long project) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    backLogService.setIntegrations(applicationConfig);
    return backLogService.getIssueTypes(project);
  }

  @GetMapping(path = "/{id}/search_backlog_issues")
  public JsonNode fetchBackLogIssues(@PathVariable("id") Long id,
                                     @NotNull @RequestParam("project") Long project,
                                     @NotNull @RequestParam("issueTypeId") Long issueTypeId,
                                     @NotNull @RequestParam("priorityId") Long priorityId,
                                     @RequestParam(value = "keyword", required = false) String keyword) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    backLogService.setIntegrations(applicationConfig);
    return backLogService.getIssuesList(project, issueTypeId, priorityId, keyword);
  }

  @GetMapping(path = "/{id}/search_backlog_issue")
  public JsonNode fetchBackLogIssue(@PathVariable("id") Long id, @NotNull @RequestParam("issueId") Long issueId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    backLogService.setIntegrations(applicationConfig);
    return backLogService.getIssue(issueId);
  }

  @GetMapping(path = "/{id}/search_backlog_priorities")
  public JsonNode fetchBackLogPriorities(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    backLogService.setIntegrations(applicationConfig);
    return backLogService.getPriorities();
  }

  @GetMapping(path = "/{id}/zepel_projects")
  public JsonNode fetchZepelProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    zepelService.setIntegrations(applicationConfig);
    return zepelService.projects();
  }

  @GetMapping(path = "/{id}/search_zepel_issue_types")
  public JsonNode fetchZepelIssueTypes(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    zepelService.setIntegrations(applicationConfig);
    return zepelService.getIssueTypes(project);
  }

  @GetMapping(path = "/{id}/search_zepel_issues")
  public JsonNode fetchZepelIssues(@PathVariable("id") Long id,
                                   @NotNull @RequestParam("project") String project,
                                   @NotNull @RequestParam("issueTypeId") String issueTypeId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    zepelService.setIntegrations(applicationConfig);
    return zepelService.getIssuesList(project, issueTypeId);
  }

  @GetMapping(path = "/{id}/youtrack_projects")
  public JsonNode fetchYoutrackProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    youtrackService.setIntegrations(applicationConfig);
    return youtrackService.projects();
  }

  @GetMapping(path = "/{id}/search_youtrack_issues")
  public JsonNode searchYoutrackIssues(@PathVariable("id") Long id, @Nullable @RequestParam("title") String title) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    this.youtrackService.setIntegrations(applicationConfig);
    return this.youtrackService.getIssuesList();
  }

  @GetMapping(path = "/{id}/bugzilla_projects")
  public JsonNode fetchBugZillaProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    bugZillaService.setIntegrations(applicationConfig);
    return bugZillaService.projects();
  }

  @GetMapping(path = "/{id}/search_bugzilla_issues")
  public JsonNode fetchBugZillaIssues(@PathVariable("id") Long id,
                                      @NotNull @RequestParam("project") String project,
                                      @NotNull @RequestParam("issueType") String issueType,
                                      @NotNull @RequestParam("version") String version) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    bugZillaService.setIntegrations(applicationConfig);
    return bugZillaService.getIssuesList(project, issueType, version);
  }

  @GetMapping(path = "/{id}/search_bugzilla_issue/{issueId}")
  public JsonNode fetchBugZillaIssue(@PathVariable("id") Long id, @PathVariable("issueId") Long issueId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    bugZillaService.setIntegrations(applicationConfig);
    return bugZillaService.getIssue(issueId);
  }

  @GetMapping(path = "/{id}/trello_projects")
  public JsonNode fetchTrelloProjects(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    trelloService.setApplicationConfig(applicationConfig);
    return trelloService.projects();
  }

  @GetMapping(path = "/{id}/search_trello_issue_types")
  public JsonNode fetchTrelloIssueTypes(@PathVariable("id") Long id, @NotNull @RequestParam("project") String project) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    trelloService.setApplicationConfig(applicationConfig);
    return trelloService.getIssueTypes(project);
  }

  @GetMapping(path = "/{id}/search_trello_issues")
  public JsonNode fetchTrelloIssues(@PathVariable("id") Long id,
                                    @NotNull @RequestParam("issueTypeId") String issueTypeId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    trelloService.setApplicationConfig(applicationConfig);
    return trelloService.getIssuesList(issueTypeId);
  }

  @GetMapping(path = "/{id}/search_trello_issue")
  public JsonNode fetchTrelloIssue(@PathVariable("id") Long id,
                                   @NotNull @RequestParam("issueId") String issueId) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    trelloService.setApplicationConfig(applicationConfig);
    return trelloService.getIssue(issueId);
  }

  @GetMapping(path = "/{id}/linear_teams")
  public JsonNode fetchLinearTeams(@PathVariable("id") Long id) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    linearService.setIntegrations(applicationConfig);
    return linearService.teams();
  }

  @GetMapping(path = "/{id}/search_linear_projects")
  public JsonNode fetchLinearProjects(@PathVariable("id") Long id, @NotNull @RequestParam("teamId") String teamId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    linearService.setIntegrations(applicationConfig);
    return linearService.projects(teamId);
  }

  @GetMapping(path = "/{id}/search_linear_issues")
  public JsonNode fetchLinearIssues(@PathVariable("id") Long id,
                                    @NotNull @RequestParam("projectId") String projectId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    linearService.setIntegrations(applicationConfig);
    return linearService.getIssuesList(projectId);
  }

  @GetMapping(path = "/{id}/search_linear_issue")
  public JsonNode fetchLinearIssue(@PathVariable("id") Long id,
                                   @NotNull @RequestParam("issueId") String issueId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    linearService.setIntegrations(applicationConfig);
    return linearService.getIssue(issueId);
  }

  @PostMapping(path = "/test_linear_integration")
  public JsonNode testLinearAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException, IOException, URISyntaxException {
    return linearService.testIntegration(config);
  }


  @GetMapping(path = "/{id}/clickup_tasks")
  public JsonNode fetchClickUpTasks(@PathVariable("id") Long id, @NotNull @RequestParam("listId") String listId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    clickUpService.setWorkspaceConfig(applicationConfig);
    return clickUpService.tasks(listId);
  }

  @GetMapping(path = "/{id}/clickup_lists")
  public JsonNode fetchClickUpLists(@PathVariable("id") Long id, @NotNull @RequestParam("folderId") String folderId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    clickUpService.setWorkspaceConfig(applicationConfig);
    return clickUpService.lists(folderId);
  }

  @GetMapping(path = "/{id}/clickup_folders")
  public JsonNode fetchClickUpFolders(@PathVariable("id") Long id, @NotNull @RequestParam("spaceId") String spaceId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    clickUpService.setWorkspaceConfig(applicationConfig);
    return clickUpService.folders(spaceId);
  }

  @GetMapping(path = "/{id}/clickup_spaces")
  public JsonNode fetchClickUpSpaces(@PathVariable("id") Long id, @NotNull @RequestParam("teamId") String teamId) throws TestsigmaException, URISyntaxException {
    Integrations applicationConfig = this.integrationsService.find(id);
    clickUpService.setWorkspaceConfig(applicationConfig);
    return clickUpService.spaces(teamId);
  }

  @GetMapping(path = "/{id}/clickup_teams")
  public JsonNode fetchClickUpTeams(@PathVariable("id") Long id) throws TestsigmaException {
    Integrations applicationConfig = this.integrationsService.find(id);
    clickUpService.setWorkspaceConfig(applicationConfig);
    return clickUpService.teams();
  }

  @PostMapping(path = "/test_clickup_integration")
  public JsonNode testClickUpAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException, IOException, URISyntaxException {
    return clickUpService.testIntegration(config);
  }

  @PostMapping(path = "/test_youtrack_integration")
  public JsonNode testYtAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return youtrackService.testIntegration(config);
  }

  @PostMapping(path = "/test_azure_integration")
  public JsonNode testAzureAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return azureService.testIntegration(config);
  }

  @PostMapping(path = "/test_mantis_integration")
  public JsonNode testMantisAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return mantisService.testIntegration(config);
  }

  @PostMapping(path = "/test_zepel_integration")
  public JsonNode testZepelAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return zepelService.testIntegration(config);
  }

  @PostMapping(path = "/test_jira_integration")
  public JsonNode testJiraAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return jiraService.testIntegration(config);
  }

  @PostMapping(path = "/test_freshrelease_integration")
  public JsonNode testFRAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return freshreleaseService.testIntegration(config);
  }

  @PostMapping(path = "/test_backlog_integration")
  public JsonNode testBacklogAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return backLogService.testIntegration(config);
  }

  @PostMapping(path = "/test_bugzilla_integration")
  public JsonNode testBugzillaAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return bugZillaService.testIntegration(config);
  }

  @PostMapping(path = "/test_trello_integration")
  public JsonNode testTrelloAuth(@RequestBody IntegrationsRequest config) throws TestsigmaException {
    return trelloService.testIntegration(config);
  }
}
