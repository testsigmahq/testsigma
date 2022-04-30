/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ApplicationCloudXMLDTO;
import com.testsigma.dto.export.ApplicationVersionCloudXMLDTO;
import com.testsigma.dto.export.ApplicationVersionXMLDTO;
import com.testsigma.dto.export.ApplicationXMLDTO;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkspaceVersionService extends XMLExportImportService<WorkspaceVersion> {
  private final WorkspaceVersionRepository workspaceVersionRepository;
  private final WorkspaceVersionMapper workspaceVersionMapper;
  private final WorkspaceVersionMapper mapper;

  public WorkspaceVersion find(Long id) throws ResourceNotFoundException {
    return this.workspaceVersionRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Workspace Version not found with id:" + id));
  }

  @Override
  List<WorkspaceVersion> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
    return null;
  }

  @Override
  Optional<WorkspaceVersion> findImportedEntity(WorkspaceVersion workspaceVersion, BackupDTO importDTO) {
    return Optional.empty();
  }

  @Override
  Optional<WorkspaceVersion> findImportedEntityHavingSameName(Optional<WorkspaceVersion> previous, WorkspaceVersion workspaceVersion, BackupDTO importDTO) throws ResourceNotFoundException {
    return Optional.empty();
  }

  @Override
  boolean hasImportedId(Optional<WorkspaceVersion> previous) {
    return false;
  }

  @Override
  boolean isEntityAlreadyImported(Optional<WorkspaceVersion> previous, WorkspaceVersion workspaceVersion) {
    return false;
  }

  @Override
  WorkspaceVersion processBeforeSave(Optional<WorkspaceVersion> previous, WorkspaceVersion present, WorkspaceVersion importEntity, BackupDTO importDTO) throws ResourceNotFoundException {
    return null;
  }

  @Override
  WorkspaceVersion copyTo(WorkspaceVersion workspaceVersion) {
    return null;
  }

  @Override
  WorkspaceVersion save(WorkspaceVersion workspaceVersion) {
    return null;
  }

  @Override
  Optional<WorkspaceVersion> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    return Optional.empty();
  }

  @Override
  boolean hasToSkip(WorkspaceVersion workspaceVersion, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(WorkspaceVersion workspaceVersion, WorkspaceVersion previous, BackupDTO importDTO) {

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


  public void setXmlImportVersionPrerequisites(BackupDTO importDTO) throws IOException {
    log.debug("setting version details version initiated");
    FileFilter applicationFilter = new RegexFileFilter("^application_\\d+.xml$");
    FileFilter workspaceFilter = new RegexFileFilter("^workspace_\\d+.xml$");
    List<Object> applicationXMLDTOS;
    File[] applicationFiles = importDTO.getDestFiles().listFiles()[0].listFiles(applicationFilter);
    File[] workspaceFiles = importDTO.getDestFiles().listFiles()[0].listFiles(workspaceFilter);
    if (applicationFiles != null && applicationFiles.length>0) {
      applicationXMLDTOS = getXMLDTOs(applicationFiles[0], new TypeReference<List<ApplicationCloudXMLDTO>>() {
      });
      ApplicationCloudXMLDTO applicationXMLDTO = (ApplicationCloudXMLDTO) applicationXMLDTOS.get(0);
      FileFilter fileFilter = new RegexFileFilter("^version_\\d+.xml$");
      File[] files = importDTO.getDestFiles().listFiles()[0].listFiles(fileFilter);
      if (files != null && files.length > 0) {
        List<Object> applicationVersionXMLDTOS = getXMLDTOs(files[0], new TypeReference<List<ApplicationVersionCloudXMLDTO>>() {
        });
        WorkspaceVersion newWorkspaceVersion = workspaceVersionRepository.findById(importDTO.getWorkspaceVersionId()).get();
        importDTO.setWorkspaceId(newWorkspaceVersion.getWorkspaceId());
        importDTO.setIsSameApplicationType(applicationXMLDTO.getWorkspaceType()
                .equals(newWorkspaceVersion.getWorkspace().getWorkspaceType()));
        importDTO.setIsSameVersion(newWorkspaceVersion.getId().equals(((ApplicationVersionCloudXMLDTO) applicationVersionXMLDTOS.get(0)).getId()));
        importDTO.setWorkspaceType(newWorkspaceVersion.getWorkspace().getWorkspaceType());
        importDTO.setIsCloudImport(true);
      }
    } else {
      applicationXMLDTOS = getXMLDTOs(workspaceFiles[0], new TypeReference<List<ApplicationXMLDTO>>() {
      });
      ApplicationXMLDTO applicationXMLDTO = (ApplicationXMLDTO) applicationXMLDTOS.get(0);
    FileFilter fileFilter = new RegexFileFilter("^version_\\d+.xml$");
    File[] files = importDTO.getDestFiles().listFiles()[0].listFiles(fileFilter);
    if (files != null && files.length > 0) {
      List<Object> applicationVersionXMLDTOS = getXMLDTOs(files[0], new TypeReference<List<ApplicationVersionXMLDTO>>() {
      });
      WorkspaceVersion newWorkspaceVersion = workspaceVersionRepository.findById(importDTO.getWorkspaceVersionId()).get();
      importDTO.setWorkspaceId(newWorkspaceVersion.getWorkspaceId());
      importDTO.setIsSameApplicationType(applicationXMLDTO.getWorkspaceType()
              .equals(newWorkspaceVersion.getWorkspace().getWorkspaceType()));
      importDTO.setIsSameVersion(newWorkspaceVersion.getId().equals(((ApplicationVersionXMLDTO) applicationVersionXMLDTOS.get(0)).getId()));
      importDTO.setWorkspaceType(newWorkspaceVersion.getWorkspace().getWorkspaceType());
      importDTO.setIsCloudImport(false);
    }
  }
    log.debug("setting version details version completed");
  }

  private List<Object> getXMLDTOs(File file, TypeReference reference) throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    String xmlData = FileUtils.readFileToString(file, "UTF-8");
    return (List<Object>) xmlMapper.readValue(xmlData, reference);
  }
}

