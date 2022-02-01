/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.BaseXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.BackupDetailMapper;
import com.testsigma.model.BackupDetail;
import com.testsigma.model.BackupStatus;
import com.testsigma.repository.BackupDetailRepository;
import com.testsigma.web.request.BackupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class BackupDetailService extends XMLExportService<BackupDetail> {

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
  private final BackupDetailMapper exportBackupEntityMapper;

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

  public BackupDetail create(BackupDetail backupDetail) {
    backupDetail.setMessage(MessageConstants.BACKUP_IS_IN_PROGRESS);
    backupDetail.setStatus(BackupStatus.IN_PROGRESS);
    backupDetail.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    backupDetail = this.repository.save(backupDetail);
    return backupDetail;
  }

  public BackupDetail save(BackupDetail backupDetail) {
    return this.repository.save(backupDetail);
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
        testcaseService.export(backupDTO);
        teststepService.export(backupDTO);
        reststepService.export(backupDTO);
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
        log.debug("backup process for completed");
      }
    }).start();
  }
}
