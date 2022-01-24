/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ApplicationVersionXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.WorkspaceVersionMapper;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.repository.WorkspaceVersionRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.VersionSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkspaceVersionService extends XMLExportService<WorkspaceVersion> {
  private final WorkspaceVersionRepository workspaceVersionRepository;
  private final RequirementService requirementService;
  private final WorkspaceVersionMapper workspaceVersionMapper;
  private final WorkspaceVersionMapper mapper;

  public WorkspaceVersion find(Long id) throws ResourceNotFoundException {
    return this.workspaceVersionRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Workspace Version not found with id:" + id));
  }

  public Page<WorkspaceVersion> findAll(Specification<WorkspaceVersion> spec, Pageable pageable) {
    return this.workspaceVersionRepository.findAll(spec, pageable);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    WorkspaceVersion version = find(id);
    this.workspaceVersionRepository.delete(version);
  }

  public WorkspaceVersion update(WorkspaceVersion version) {
    return this.workspaceVersionRepository.save(version);
  }

  public WorkspaceVersion create(WorkspaceVersion version) {
    version = workspaceVersionRepository.save(version);
    return version;
  }

  public WorkspaceVersion findFirstByWorkspaceId(Long workspaceId) {
    return this.workspaceVersionRepository.findFirstByWorkspaceId(workspaceId);
  }

  public WorkspaceVersion copy(WorkspaceVersion version, Long versionId) throws ResourceNotFoundException, TestsigmaDatabaseException {
    WorkspaceVersion originalVersion = find(versionId);
    WorkspaceVersion copiedVersion = workspaceVersionMapper.copy(originalVersion);
    copiedVersion.setVersionName(version.getVersionName());
    copiedVersion.setCreatedDate(new Timestamp(new Date().getTime()));
    copiedVersion.setUpdatedDate(null);
    copiedVersion.setId(null);
    copiedVersion = workspaceVersionRepository.save(copiedVersion);
    Long newVersionId = copiedVersion.getId();
//    Long userId = CommonUtil.getLoggedInUserId();

    workspaceVersionRepository.copyRequirementDetails(newVersionId, versionId);
    workspaceVersionRepository.copyTestCaseDetails(newVersionId, versionId);
    workspaceVersionRepository.copyTestStepDetails(newVersionId);
    workspaceVersionRepository.copyRestStepDetails(newVersionId);
    workspaceVersionRepository.copyConditionalDetails(newVersionId);
    workspaceVersionRepository.copyElementScreenNames(newVersionId, versionId);
    workspaceVersionRepository.copyFields(newVersionId, versionId);
    workspaceVersionRepository.copyTestData(newVersionId, versionId);
    workspaceVersionRepository.updateTestDataReference(newVersionId);
    workspaceVersionRepository.updateStepForLoopTestData(newVersionId);
    workspaceVersionRepository.updateStepGroupReference(newVersionId);
    workspaceVersionRepository.updateTestCasePreRequisiteReference(newVersionId);
    workspaceVersionRepository.updateStepPreRequirementReference(newVersionId);
    workspaceVersionRepository.copyTestSuites(newVersionId, versionId);
    workspaceVersionRepository.copyTestSuitePrerequisites(newVersionId);
    workspaceVersionRepository.copyGroupTestcaseMappings(newVersionId, versionId);
    workspaceVersionRepository.copyTestPlansFromVersion(newVersionId, versionId);
    workspaceVersionRepository.copyTestDevices(newVersionId, versionId);
    workspaceVersionRepository.copyTestDeviceGroupMappings(newVersionId, versionId);
    workspaceVersionRepository.copyTestSuiteLabels(newVersionId);
    workspaceVersionRepository.copyTestcaseLabels(newVersionId);
    workspaceVersionRepository.copyElementLabels(newVersionId);
    workspaceVersionRepository.copyTestPlanLabels(newVersionId);

    return copiedVersion;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    log.debug("backup process for version initiated");
    writeXML("version", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for version completed");
  }

  @Override
  protected List<ApplicationVersionXMLDTO> mapToXMLDTOList(List<WorkspaceVersion> list) {
    return mapper.mapVersions(list);
  }

  public Specification<WorkspaceVersion> getExportXmlSpecification(BackupDTO backupDTO) {
    Optional<WorkspaceVersion> applicationVersion = workspaceVersionRepository.findById(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("id", SearchOperation.EQUALITY, applicationVersion.get().getId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    VersionSpecificationsBuilder versionSpecificationsBuilder = new VersionSpecificationsBuilder();
    versionSpecificationsBuilder.params = params;
    return versionSpecificationsBuilder.build();
  }

}

