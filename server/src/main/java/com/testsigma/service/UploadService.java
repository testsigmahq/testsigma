/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.UploadDTO;
import com.testsigma.dto.export.UploadXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.UploadEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.UploadMapper;
import com.testsigma.model.*;
import com.testsigma.repository.UploadRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.UploadSpecificationsBuilder;
import com.testsigma.tasks.ReSignTask;
import com.testsigma.web.request.UploadRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class UploadService extends XMLExportService<Upload> {
  private final WebApplicationContext webApplicationContext;
  private final UploadRepository uploadRepository;
  private final StorageServiceFactory storageServiceFactory;
  private final ProvisioningProfileUploadService profileUploadService;
  private final ProvisioningProfileDeviceService profileDeviceService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final WorkspaceService workspaceService;
  private final WorkspaceVersionService workspaceVersionService;
  private final UploadMapper mapper;

  public Upload find(Long id) throws ResourceNotFoundException {
    Upload upload = this.uploadRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Couldn't find upload with " +
        "id: " + id));
    upload.setPreSignedURL(this.getPreSignedURL(upload));
    return upload;
  }

  public Page<Upload> findAll(Specification<Upload> specification, Pageable pageable) {
    return uploadRepository.findAll(specification, pageable);
  }

  public List<Upload> findAllByType(UploadType type) {
    return this.uploadRepository.findAllByType(type);
  }

  public Upload create(UploadRequest uploadRequest) throws TestsigmaException {
    Upload upload = new Upload();
    upload.setType(uploadRequest.getUploadType());
    upload.setName(uploadRequest.getName());
    upload.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    upload.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    upload.setUploadStatus(UploadStatus.InProgress);
    upload.setWorkspaceId(uploadRequest.getWorkspaceId());
    upload = saveAndUpload(upload, uploadRequest);
    publishEvent(upload, EventType.CREATE);
    return upload;
  }

  public Upload update(Upload upload, UploadRequest uploadRequest) throws TestsigmaException {
    upload.setName(uploadRequest.getName());
    upload.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    if (uploadRequest.getFileContent() == null || (uploadRequest.getFileContent() != null && uploadRequest.getFileContent().getOriginalFilename().equals("")))
      return this.update(upload);
    else {
      upload.setUploadStatus(UploadStatus.InProgress);
      return saveAndUpload(upload, uploadRequest);
    }
  }

  public Upload update(Upload upload) throws TestsigmaException {
    upload = this.uploadRepository.save(upload);
    publishEvent(upload, EventType.UPDATE);
    return upload;
  }

  public void delete(Upload upload) {
    this.uploadRepository.delete(upload);
    publishEvent(upload, EventType.DELETE);
  }

  public String getPreSignedURL(Upload upload) {
    URL newPreSignedURL = storageServiceFactory.getStorageService().generatePreSignedURL(upload.getAppPath(), StorageAccessLevel.READ, 300);
    return newPreSignedURL.toString();
  }

  public void uploadFile(File uploadedFile, Upload upload) throws TestsigmaException {
    try {
      String originalFileName = ObjectUtils.defaultIfNull(upload.getFileName(), "tmp")
        .replaceAll("\\s+", "_");
      StringBuilder storageFilePath =
        new StringBuilder().append("/uploads/").append(upload.getId()).append("/").append(originalFileName);

      uploadToStorage(storageFilePath.toString(), uploadedFile, upload);
      upload.setAppPath(storageFilePath.toString());
      this.uploadRepository.save(upload);
      resignTheUpload(upload);
      publishEvent(upload, EventType.UPDATE);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private void uploadToStorage(String filePathInStorageService, File fileToUpload, Upload upload) {
    try {
      log.info(String.format("Uploading file:%s to storage path %s", fileToUpload.getAbsolutePath(), filePathInStorageService));
      storageServiceFactory.getStorageService().addFile(filePathInStorageService, fileToUpload);
      upload.setUploadStatus(UploadStatus.Completed);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      upload.setUploadStatus(UploadStatus.Failed);
    }
  }

  public List<UploadDTO> setSignedFlag(List<UploadDTO> uploadDTOS, UploadSpecificationsBuilder builder) {
    for (SearchCriteria searchCriteria : builder.params) {
      if (searchCriteria.getKey().equals("deviceId")) {
        Long deviceId = Long.parseLong(searchCriteria.getValue().toString());
        ProvisioningProfileDevice profileDevice = profileDeviceService.findByAgentDeviceId(deviceId);
        if (profileDevice != null) {
          for (UploadDTO uploadDTO : uploadDTOS) {
            if (uploadDTO.getType() == UploadType.IPA) {
              ProvisioningProfileUpload profileUpload = profileUploadService.findByDeviceIdAndUploadId(deviceId,
                uploadDTO.getId());
              uploadDTO.setSigned((profileUpload != null));
            }
          }
        } else {
          log.info("The device is not provisioned. Unless the device is provisioned and upload file is resigned it can't used");
        }
      }
    }
    return uploadDTOS;
  }

  private Upload saveAndUpload(Upload upload, UploadRequest uploadRequest) throws TestsigmaException {
    MultipartFile uploadedMultipartFile = uploadRequest.getFileContent();
    File uploadedFile = copyUploadToTempFile(uploadedMultipartFile);
    upload.setFileSize((int) uploadedMultipartFile.getSize());
    upload.setFileName(ObjectUtils.defaultIfNull(uploadedMultipartFile.getOriginalFilename(), "tmp")
      .replaceAll("\\s+", "_"));
    upload = this.uploadRepository.save(upload);

    if (uploadRequest.getFileContent() != null && uploadRequest.getFileContent().getSize() != 0)
      uploadFile(uploadedFile, upload);
    return upload;
  }

  private File copyUploadToTempFile(MultipartFile uploadedFile) throws TestsigmaException {
    try {
      String fileName = uploadedFile.getOriginalFilename().replaceAll("\\s+", "_");
      String fileBaseName = FilenameUtils.getBaseName(fileName);
      String extension = FilenameUtils.getExtension(fileName);
      if (StringUtils.isNotBlank(extension)) {
        extension = "." + extension;
      }
      File tempFile = File.createTempFile(fileBaseName + "_", extension);
      log.info("Transferring uploaded multipart file to - " + tempFile.getAbsolutePath());
      uploadedFile.transferTo(tempFile.toPath());
      return tempFile;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void resignTheUpload(Upload upload) {
    if (upload.getType() == UploadType.IPA) {
      ReSignTask reSignTask = new ReSignTask(webApplicationContext, null, upload);
      ReSignTaskFactory.getInstance().startTask(reSignTask);
    } else {
      log.info(String.format("Upload Type - [%s]. Skipping iOS app resign upload task...", upload.getType()));
    }
  }

  public void publishEvent(Upload upload, EventType eventType) {
    UploadEvent<Upload> event = createEvent(upload, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public UploadEvent<Upload> createEvent(Upload upload, EventType eventType) {
    UploadEvent<Upload> event = new UploadEvent<>();
    event.setEventData(upload);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsUploadsEnabled()) return;
    log.debug("backup process for upload initiated");
    writeXML("uploads", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for upload completed");
  }

  public Specification<Upload> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    WorkspaceVersion applicationVersion = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("workspaceId", SearchOperation.EQUALITY, applicationVersion.getWorkspaceId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    UploadSpecificationsBuilder uploadSpecificationsBuilder = new UploadSpecificationsBuilder();
    uploadSpecificationsBuilder.params = params;
    return uploadSpecificationsBuilder.build();
  }

  @Override
  protected List<UploadXMLDTO> mapToXMLDTOList(List<Upload> list) {
    return mapper.mapUploads(list, this, null);
  }

  @Override
  protected List<UploadXMLDTO> mapToXMLDTOList(List<Upload> list, BackupDTO backupDTO) {
    return mapper.mapUploads(list, this, backupDTO.getSrcFiles());
  }
}
