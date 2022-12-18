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
import com.testsigma.model.EntityExternalMapping;
import com.testsigma.model.EntityType;
import com.testsigma.model.Integrations;
import com.testsigma.repository.EntityExternalMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EntityExternalMappingService {
  private final EntityExternalMappingRepository repository;
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
  private final XrayCloudService xrayCloudService;

  public Optional<EntityExternalMapping> findByEntityIdAndEntityType(Long entityId, EntityType entityType, Long applicationId){
    return this.repository.findByEntityIdAndEntityTypeAndApplicationId(entityId, entityType, applicationId);
  }

  public List<EntityExternalMapping> findByExternalIdAndEntityTypeAndApplicationId(String externalId, EntityType entityType, Long applicationId){
    return this.repository.findByExternalIdAndEntityTypeAndApplicationId(externalId, entityType, applicationId);
  }

  public Optional<EntityExternalMapping> findByEntityIdAndEntityType(String entityId, EntityType entityType){
    return this.repository.findByEntityIdAndEntityType(entityId, entityType);
  }

  public List<EntityExternalMapping> findAllByEntityIdAndEntityType(Long entityId, EntityType entityType){
    return this.repository.findAllByEntityIdAndEntityType(entityId, entityType);
  }

  public List<EntityExternalMapping> findByEntityIds(Long[] ids, EntityType entityType, Long applicationId){
    return this.repository.findByEntityIds(ids, entityType, applicationId);
  }

  public Page<EntityExternalMapping> findAll(Specification<EntityExternalMapping> specification, Pageable pageable) {
    return this.repository.findAll(specification, pageable);
  }

  public EntityExternalMapping create(EntityExternalMapping mapping)
    throws TestsigmaException, IOException, URISyntaxException {
    Integrations config = this.applicationConfigService.find(mapping.getApplicationId());
    mapping.setApplication(config);
    if(mapping.getEntityType() == EntityType.TEST_CASE_RESULT) {
      mapping.setTestCaseResult(testCaseResultService.find(mapping.getEntityId()));
    }
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
    } else if(config.getWorkspace().isXrayCloud()){
      xrayCloudService.link(mapping);
    }
    return this.repository.save(mapping);
  }

  public EntityExternalMapping find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Missing with id" + id));
  }

  public void destroy(EntityExternalMapping mapping) throws TestsigmaException, IOException {
    if (mapping.getApplication().getWorkspace().isJira()) {
      jiraService.setIntegrations(mapping.getApplication());
      jiraService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isFreshrelease()) {
      freshreleaseService.setIntegrations(mapping.getApplication());
      freshreleaseService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isMantis()) {
      mantisService.setIntegrations(mapping.getApplication());
      mantisService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isAzure()) {
      azureService.setApplicationConfig(mapping.getApplication());
      azureService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isBackLog()) {
      backLogService.setIntegrations(mapping.getApplication());
      backLogService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isZepel()) {
      zepelService.setIntegrations(mapping.getApplication());
      zepelService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isBugZilla()) {
      bugZillaService.setIntegrations(mapping.getApplication());
      bugZillaService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isTrello()) {
      trelloService.setApplicationConfig(mapping.getApplication());
      trelloService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isLinear()) {
      linearService.setIntegrations(mapping.getApplication());
      linearService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isYoutrack()) {
      youtrackService.setIntegrations(mapping.getApplication());
      youtrackService.unlink(mapping);
    } else if (mapping.getApplication().getWorkspace().isClickUp()) {
      clickUpService.setWorkspaceConfig(mapping.getApplication());
      clickUpService.unlink(mapping);
    }

    this.repository.delete(mapping);
  }

  public EntityExternalMapping save(EntityExternalMapping mapping){
    return this.repository.save(mapping);
  }

  public EntityExternalMapping fetch(Long id) throws TestsigmaException, IOException {
    EntityExternalMapping mapping = this.find(id);
    jiraService.setIntegrations(mapping.getApplication());
    if (mapping.getApplication().getWorkspace().isJira())
      mapping.setFields(jiraService.fetchIssue(mapping));
    else if (mapping.getApplication().getWorkspace().isFreshrelease()) {
      freshreleaseService.setIntegrations(mapping.getApplication());
      mapping.setFields(freshreleaseService.fetchIssue(mapping));
    } else if (mapping.getApplication().getWorkspace().isAzure()) {
      azureService.setApplicationConfig(mapping.getApplication());
      mapping.setFields(azureService.fetchIssue(mapping));
    } else if (mapping.getApplication().getWorkspace().isZepel()) {
      zepelService.setIntegrations(mapping.getApplication());
      mapping.setFields(zepelService.fetchIssue(mapping));
    } else if (mapping.getApplication().getWorkspace().isYoutrack()) {
      youtrackService.setIntegrations(mapping.getApplication());
      mapping.setFields(youtrackService.fetchIssue(mapping));
    } else if (mapping.getApplication().getWorkspace().isClickUp()) {
      clickUpService.setWorkspaceConfig(mapping.getApplication());
      mapping.setFields(clickUpService.fetchIssue(mapping));
    }
    return mapping;
  }
}

