/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.Integrations;
import com.testsigma.model.TestCaseResultExternalMapping;
import com.testsigma.repository.TestCaseResultExternalMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCaseResultExternalMappingService {
  private final TestCaseResultExternalMappingRepository repository;
  private final IntegrationsService applicationConfigService;
  private final JiraService jiraService;
  private final FreshreleaseService freshreleaseService;
  private final TestCaseResultService testCaseResultService;
  private final MantisService mantisService;
  private final AzureService azureService;
  private final BackLogService backLogService;
  private final ZepelService zepelService;
  private final YoutrackService youtrackService;
  private final BugZillaService bugZillaService;
  private final TrelloService trelloService;
  private final LinearService linearService;
  private final ClickUpService clickUpService;

  public List<TestCaseResultExternalMapping> findByResultId(Long resultId) {
    return this.repository.findByTestCaseResultId(resultId);
  }

  public TestCaseResultExternalMapping create(TestCaseResultExternalMapping mapping)
    throws TestsigmaException, IOException, URISyntaxException {
    Integrations config = this.applicationConfigService.find(mapping.getWorkspaceId());
    mapping.setWorkspace(config);
    mapping.setTestCaseResult(testCaseResultService.find(mapping.getTestCaseResultId()));
    if (config.getWorkspace().isJira()) {
      jiraService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? jiraService.link(mapping) : jiraService.addIssue(mapping);
    } else if (config.getWorkspace().isFreshrelease()) {
      freshreleaseService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? freshreleaseService.link(mapping) : freshreleaseService.addIssue(mapping);
    } else if (config.getWorkspace().isAzure()) {
      mapping = mapping.getLinkToExisting() ? azureService.link(mapping) : azureService.addIssue(mapping);
    } else if (config.getWorkspace().isMantis()) {
      mantisService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? mantisService.link(mapping) : mantisService.addIssue(mapping);
    } else if (config.getWorkspace().isBackLog()) {
      backLogService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? backLogService.link(mapping) : backLogService.addIssue(mapping);
    } else if (config.getWorkspace().isZepel()) {
      zepelService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? zepelService.link(mapping) : zepelService.addIssue(mapping);
    } else if (config.getWorkspace().isYoutrack()) {
      youtrackService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? youtrackService.link(mapping) : youtrackService.addIssue(mapping);
    } else if (config.getWorkspace().isBugZilla()) {
      bugZillaService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? bugZillaService.link(mapping) : bugZillaService.addIssue(mapping);
    } else if (config.getWorkspace().isTrello()) {
      trelloService.setApplicationConfig(config);
      mapping = mapping.getLinkToExisting() ? trelloService.link(mapping) : trelloService.addIssue(mapping);
    } else if (config.getWorkspace().isLinear()) {
      linearService.setIntegrations(config);
      mapping = mapping.getLinkToExisting() ? linearService.link(mapping) : linearService.addIssue(mapping);
    } else if (config.getWorkspace().isClickUp()) {
      clickUpService.setWorkspaceConfig(config);
      mapping = mapping.getLinkToExisting() ? clickUpService.link(mapping) : clickUpService.addIssue(mapping);
    }
    return this.repository.save(mapping);
  }

  public TestCaseResultExternalMapping find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Missing with id" + id));
  }

  public void destroy(TestCaseResultExternalMapping mapping) throws TestsigmaException, IOException {
    if (mapping.getWorkspace().getWorkspace().isJira()) {
      jiraService.setIntegrations(mapping.getWorkspace());
      jiraService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isFreshrelease()) {
      freshreleaseService.setIntegrations(mapping.getWorkspace());
      freshreleaseService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isMantis()) {
      mantisService.setIntegrations(mapping.getWorkspace());
      mantisService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isAzure()) {
      azureService.setApplicationConfig(mapping.getWorkspace());
      azureService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isBackLog()) {
      backLogService.setIntegrations(mapping.getWorkspace());
      backLogService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isZepel()) {
      zepelService.setIntegrations(mapping.getWorkspace());
      zepelService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isBugZilla()) {
      bugZillaService.setIntegrations(mapping.getWorkspace());
      bugZillaService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isTrello()) {
      trelloService.setApplicationConfig(mapping.getWorkspace());
      trelloService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isLinear()) {
      linearService.setIntegrations(mapping.getWorkspace());
      linearService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isYoutrack()) {
      youtrackService.setIntegrations(mapping.getWorkspace());
      youtrackService.unlink(mapping);
    } else if (mapping.getWorkspace().getWorkspace().isClickUp()) {
      clickUpService.setWorkspaceConfig(mapping.getWorkspace());
      clickUpService.unlink(mapping);
    }

    this.repository.delete(mapping);
  }

  public TestCaseResultExternalMapping fetch(Long id) throws TestsigmaException, IOException {
    TestCaseResultExternalMapping mapping = this.find(id);
    jiraService.setIntegrations(mapping.getWorkspace());
    if (mapping.getWorkspace().getWorkspace().isJira())
      mapping.setFields(jiraService.fetchIssue(mapping));
    else if (mapping.getWorkspace().getWorkspace().isFreshrelease()) {
      freshreleaseService.setIntegrations(mapping.getWorkspace());
      mapping.setFields(freshreleaseService.fetchIssue(mapping));
    } else if (mapping.getWorkspace().getWorkspace().isAzure()) {
      azureService.setApplicationConfig(mapping.getWorkspace());
      mapping.setFields(azureService.fetchIssue(mapping));
    } else if (mapping.getWorkspace().getWorkspace().isZepel()) {
      zepelService.setIntegrations(mapping.getWorkspace());
      mapping.setFields(zepelService.fetchIssue(mapping));
    } else if (mapping.getWorkspace().getWorkspace().isYoutrack()) {
      youtrackService.setIntegrations(mapping.getWorkspace());
      mapping.setFields(youtrackService.fetchIssue(mapping));
    } else if (mapping.getWorkspace().getWorkspace().isClickUp()) {
      clickUpService.setWorkspaceConfig(mapping.getWorkspace());
      mapping.setFields(clickUpService.fetchIssue(mapping));
    }
    return mapping;
  }
}

