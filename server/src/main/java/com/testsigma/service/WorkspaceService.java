/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ApplicationXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.WorkspaceMapper;
import com.testsigma.model.Workspace;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.repository.WorkspaceRepository;
import com.testsigma.specification.ApplicationSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkspaceService extends XMLExportService<Workspace> {
  private final WorkspaceRepository workspaceRepository;
  private final WorkspaceVersionService workspaceVersionService;
  private final WorkspaceMapper mapper;

  public Workspace find(Long id) throws ResourceNotFoundException {
    return this.workspaceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workspace missing with id" + id));
  }

  public Page<Workspace> findAll(Specification<Workspace> spec, Pageable pageable) {
    return this.workspaceRepository.findAll(spec, pageable);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    Workspace workspace = find(id);
    this.workspaceRepository.delete(workspace);
  }

  public Workspace update(Workspace workspace) {
    return this.workspaceRepository.save(workspace);
  }

  public Workspace create(Workspace workspace, Boolean createDefaultVersion) {
    workspace = this.workspaceRepository.save(workspace);
    if (createDefaultVersion) {
      WorkspaceVersion version = new WorkspaceVersion();
      version.setVersionName("1.0");
      version.setCreatedDate(workspace.getCreatedDate());
      version.setWorkspaceId(workspace.getId());
      version.setWorkspace(workspace);
      workspaceVersionService.create(version);
    }
    return workspace;
  }

  public Workspace findFirstWebDemoApplication() {
    return this.workspaceRepository.findFirstByIsDemoAndWorkspaceType(true, WorkspaceType.WebApplication);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    log.debug("backup process for workspace initiated");
    writeXML("workspace", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for workspace completed");
  }

  @Override
  protected List<ApplicationXMLDTO> mapToXMLDTOList(List<Workspace> list) {
    return mapper.mapApplications(list);
  }

  public Specification<Workspace> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    WorkspaceVersion applicationVersion = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("id", SearchOperation.EQUALITY, applicationVersion.getWorkspaceId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    ApplicationSpecificationsBuilder applicationSpecificationsBuilder = new ApplicationSpecificationsBuilder();
    applicationSpecificationsBuilder.params = params;
    return applicationSpecificationsBuilder.build();
  }
}
