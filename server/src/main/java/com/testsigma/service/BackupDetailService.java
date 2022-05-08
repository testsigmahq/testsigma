/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.*;
import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.BaseXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.BackupDetailMapper;
import com.testsigma.repository.BackupDetailRepository;
import com.testsigma.web.request.BackupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class BackupDetailService extends XMLExportImportService<BackupDetail> {

  private final BackupDetailRepository repository;
  private final StorageServiceFactory storageServiceFactory;
  private final AgentService agentService;
  private final WorkspaceService workspaceService;
  private final AttachmentService attachmentService;
  private final TestDeviceService testDeviceService;
  private final TestPlanService testPlanService;
  private final RestStepService reststepService;
  private final TestCaseService testcaseService;
  private final TestCasePriorityService testCasePriorityService;
  private final TestCaseTypeService testCaseTypeService;
  private final TestDataProfileService testDataProfileService;
  private final TestStepService teststepService;
  private final ElementService elementService;
  private final WorkspaceVersionService versionService;
  private final ElementScreenService elementScreenService;
  private final UploadService uploadService;
  private final UploadVersionService uploadVersionService;
  private final BackupDetailMapper exportBackupEntityMapper;
  private final TestSuiteService testSuiteService;

  @Value("${unzip.dir}")
  private String unzipDir;

  public BackupDetail find(Long id) throws ResourceNotFoundException {
    return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Backup is not found with id:" + id));
  }

  public Page<BackupDetail> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public Optional<URL> downLoadURL(BackupDetail backupDetail) {
    return storageServiceFactory.getStorageService().generatePreSignedURLIfExists(
      "/backup/" + backupDetail.getName(), StorageAccessLevel.READ, 300);
  }

  public Optional<URL> getTestCasesPreSignedURL(BackupDetail backupDetail) {
    return storageServiceFactory.getStorageService().generatePreSignedURLIfExists(backupDetail.getAffectedCasesListPath(),
            StorageAccessLevel.READ, 300);
  }

  public BackupDetail create(BackupDetail backupDetail) {
    backupDetail.setMessage(MessageConstants.BACKUP_IS_IN_PROGRESS);
    backupDetail.setStatus(BackupStatus.IN_PROGRESS);
    backupDetail.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    backupDetail = this.repository.save(backupDetail);
    return backupDetail;
  }

  public void create(BackupRequest request, MultipartFile file) throws IOException {

    BackupDTO importDTO = exportBackupEntityMapper.map(request);
    BackupDetail backupDetail = exportBackupEntityMapper.map(importDTO);
    backupDetail.setMessage(MessageConstants.BACKUP_IS_IN_PROGRESS);
    backupDetail.setStatus(BackupStatus.IN_PROGRESS);
    backupDetail.setActionType(BackupActionType.EXPORT);

    if (file != null && !file.isEmpty()) {
      backupDetail.setMessage(MessageConstants.IMPORT_IS_IN_PROGRESS);
      backupDetail.setStatus(BackupStatus.IN_PROGRESS);
      backupDetail.setActionType(BackupActionType.IMPORT);
      backupDetail = this.repository.save(backupDetail);
      try {
        File uploadedFile = this.copyZipFileToTemp(file);
        this.uploadImportFileToStorage(uploadedFile, backupDetail);
        this.importBackup(backupDetail);
      } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
    } else {
      backupDetail = this.repository.save(backupDetail);
    }

  }

  public BackupDetail save(BackupDetail backupDetail) {
    return this.repository.save(backupDetail);
  }

  @Override
  Optional<BackupDetail> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    return Optional.empty();
  }

  @Override
  boolean hasToSkip(BackupDetail backupDetail, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(BackupDetail backupDetail, BackupDetail previous, BackupDTO importDTO) {

  }


  public void destroy(Long id) throws ResourceNotFoundException {
    BackupDetail detail = this.find(id);
    this.repository.delete(detail);
  }

  @Override
  protected Page<BackupDetail> findAll(Specification<BackupDetail> specification, Pageable pageRequest) throws ResourceNotFoundException {
    return null;
  }

  @Override
  protected List<? extends BaseXMLDTO> mapToXMLDTOList(List<BackupDetail> list) {
    return null;
  }

  @Override
  public Specification<BackupDetail> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    return null;
  }

  public void export(BackupRequest request) throws IOException, TestsigmaException {
    BackupDTO backupDTORequest = exportBackupEntityMapper.map(request);
    BackupDetail backupDetailRequest = exportBackupEntityMapper.map(backupDTORequest);
    final BackupDetail backupDetail = create(backupDetailRequest);
    final BackupDTO backupDTO = exportBackupEntityMapper.mapTo(backupDetail);
    log.debug("initiating backup - " + backupDetail.getId());

    new Thread(() -> {
      try {
        log.debug("backup process started for ::" + backupDetail.getId());
        initExportFolder(backupDTO);
        workspaceService.export(backupDTO);
        versionService.export(backupDTO);
        testCasePriorityService.export(backupDTO);
        testCaseTypeService.export(backupDTO);
        elementScreenService.export(backupDTO);
        elementService.export(backupDTO);
        testDataProfileService.export(backupDTO);
        attachmentService.export(backupDTO);
        agentService.export(backupDTO);
        uploadService.export(backupDTO);
        uploadVersionService.export(backupDTO);
        testcaseService.export(backupDTO);
        teststepService.export(backupDTO);
        reststepService.export(backupDTO);
        testSuiteService.export(backupDTO);
        testPlanService.export(backupDTO);
        testDeviceService.export(backupDTO);
        backupDetail.setSrcFiles(backupDTO.getSrcFiles());
        backupDetail.setDestFiles(backupDTO.getDestFiles());
        exportToStorage(backupDetail);
        log.debug("backup process export completed");
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        backupDetail.setStatus(BackupStatus.FAILURE);
        backupDetail.setMessage(e.getMessage());
        repository.save(backupDetail);
        destroy(backupDTO);
      } catch (Error error) {
        log.error(error.getMessage(), error);
      } finally {
        destroy(backupDTO);
        log.debug("backup process completed");
      }
    }).start();
  }

  public void importBackup(BackupDetail importOp) throws IOException, TestsigmaException {
    log.debug("initiating import - " + importOp.getId());
    final BackupDTO importDTO = exportBackupEntityMapper.mapTo(importOp);
    new Thread(() -> {
      try {
        log.debug("import process started for ::" + importOp.getId());
        importDTO.setImportFileUrl(storageServiceFactory.getStorageService().generatePreSignedURLIfExists(
                "/backup/" + importDTO.getName(), StorageAccessLevel.READ, 300).get().toString());
        initImportFolder(importDTO,unzipDir);
       // workspaceService.setXmlImportVersionPrerequisites(importDTO);
        versionService.setXmlImportVersionPrerequisites(importDTO);
        testCasePriorityService.importXML(importDTO);
        testCaseTypeService.importXML(importDTO);
        elementScreenService.importXML(importDTO);
        elementService.importXML(importDTO);
        testDataProfileService.importXML(importDTO);
        attachmentService.importXML(importDTO);
        agentService.importXML(importDTO);
        uploadService.importXML(importDTO);
        uploadVersionService.importXML(importDTO);
        testcaseService.importXML(importDTO);
        teststepService.importXML(importDTO);
        reststepService.importXML(importDTO);
        testSuiteService.importXML(importDTO);
        testPlanService.importXML(importDTO);
        testDeviceService.importXML(importDTO);
        importOp.setAffectedCasesListPath(importDTO.getAffectedCasesListPath());
        updateSuccess(importOp);
        log.debug("import process completed");
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        importOp.setStatus(BackupStatus.FAILURE);
        importOp.setMessage(e.getMessage());
        repository.save(importOp);
        XMLExportImportService.destroyImport(importDTO);;
      } catch (Error error) {
        log.error(error.getMessage(), error);
      } finally {
        XMLExportImportService.destroyImport(importDTO);
        log.debug("import process completed");
      }
    }).start();
  }

  private void updateSuccess(BackupDetail backupDetail) {
    backupDetail.setStatus(BackupStatus.SUCCESS);
    backupDetail.setMessage(MessageConstants.IMPORT_IS_SUCCESS);
    this.save(backupDetail);
  }

  @Override
  List<BackupDetail> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
    return null;
  }

  @Override
  Optional<BackupDetail> findImportedEntity(BackupDetail backupDetail, BackupDTO importDTO) {
    return Optional.empty();
  }

  @Override
  Optional<BackupDetail> findImportedEntityHavingSameName(Optional<BackupDetail> previous, BackupDetail backupDetail, BackupDTO importDTO) throws ResourceNotFoundException {
    return Optional.empty();
  }

  @Override
  boolean hasImportedId(Optional<BackupDetail> previous) {
    return false;
  }

  @Override
  boolean isEntityAlreadyImported(Optional<BackupDetail> previous, BackupDetail backupDetail) {
    return false;
  }

  @Override
  BackupDetail processBeforeSave(Optional<BackupDetail> previous, BackupDetail present, BackupDetail importEntity, BackupDTO importDTO) throws ResourceNotFoundException {
    return null;
  }

  @Override
  BackupDetail copyTo(BackupDetail backupDetail) {
    return null;
  }

}