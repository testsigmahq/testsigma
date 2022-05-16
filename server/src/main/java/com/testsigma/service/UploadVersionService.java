/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.UploadVersionXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.UploadVersionEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.UploadMapper;
import com.testsigma.model.*;
import com.testsigma.repository.UploadVersionRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.UploadVersionSpecificationsBuilder;
import com.testsigma.tasks.ReSignTask;
import com.testsigma.util.HttpClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
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
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class UploadVersionService extends XMLExportImportService<UploadVersion> {
  private final WebApplicationContext webApplicationContext;
  private final StorageServiceFactory storageServiceFactory;
  private final ProvisioningProfileUploadService profileUploadService;
  private final ProvisioningProfileDeviceService profileDeviceService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final WorkspaceVersionService workspaceVersionService;
  private final UploadMapper mapper;
  private final UploadVersionRepository uploadVersionRepository;
  private final UploadService uploadService;
  private final HttpClient client;
  private final AppParserService appParserService;

  @Getter
  @Setter
  @Value("${server.url}")
  private String serverURL;

  public UploadVersion find(Long id) throws ResourceNotFoundException {
    UploadVersion uploadVersion = this.uploadVersionRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Couldn't find upload version with " +
        "id: " + id));
    uploadVersion.setPreSignedURL(this.getPreSignedURL(uploadVersion));
    return uploadVersion;
  }

  public List<UploadVersion> findAllByLastUploadedTimeBeforeAndUploadTypeIn(Timestamp timestamp, Collection<UploadType> uploadType) {
    return this.uploadVersionRepository.findAllByLastUploadedTimeBeforeAndUploadTypeIn(timestamp, uploadType);
  }

  public List<UploadVersion> findValidUploadsByUploadTypesIn(Collection<UploadType> uploadType) {
    return this.uploadVersionRepository.findAllByUploadTypeIn(uploadType);
  }



  public UploadVersion update(UploadVersion uploadVersion) {
    return this.uploadVersionRepository.save(uploadVersion);
  }

  public String getPreSignedURL(UploadVersion uploadVersion) {
    URL newPreSignedURL =
            storageServiceFactory.getStorageService().generatePreSignedURL(
        uploadVersion.getPath(),
                    StorageAccessLevel.READ, 300);
    return newPreSignedURL.toString();
  }

  public void uploadFile(File uploadedFile, UploadVersion uploadVersion) throws TestsigmaException {

    try {
      String storageFilePath = uploadVersion.getS3Path();
      uploadToStorage(storageFilePath, uploadedFile, uploadVersion);
      UploadVersionAppInfo uploadedVersion = appParserService.parseFile(uploadedFile);
      uploadVersion.setPath(storageFilePath);
      uploadVersion.setActivity(uploadedVersion.getAppActivity());
      uploadVersion.setPackageName(uploadedVersion.getPackageName());
      uploadVersion.setVersionName(uploadedVersion.getAppVersion());
      uploadVersion.setBundleId(uploadedVersion.getBundleId());
      this.uploadVersionRepository.save(uploadVersion);
      resignTheUpload(uploadVersion);
      publishEvent(uploadVersion, EventType.UPDATE);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private void updateUploadWithLatestUploadVersion(UploadVersion uploadVersion,
                                                   Long uploadId) throws TestsigmaException {
    Upload upload = uploadService.findById(uploadId);
    upload.setLatestVersionId(uploadVersion.getId());
    upload.setLatestVersion(uploadVersion);
    uploadService.update(upload);
  }


  private void uploadToStorage(String filePathInStorageService, File fileToUpload, UploadVersion upload) {
    try {
      log.info(String.format("Uploading file:%s to storage path %s", fileToUpload.getAbsolutePath(), filePathInStorageService));
      storageServiceFactory.getStorageService().addFile(filePathInStorageService, fileToUpload);
      upload.setUploadStatus(UploadStatus.Completed);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      upload.setUploadStatus(UploadStatus.Failed);
    }
  }



  public List<UploadVersion> setSignedFlag(List<UploadVersion> versions, Long deviceId) {
    ProvisioningProfileDevice profileDevice = profileDeviceService.findByAgentDeviceId(deviceId);
    if (profileDevice != null) {
      for (UploadVersion version : versions) {
        if (version.getUploadType() == UploadType.IPA) {
          ProvisioningProfileUpload profileUpload = profileUploadService.findByDeviceIdAndUploadId(deviceId,
            version.getUploadId());
          version.setSigned((profileUpload != null));
        }
      }
    } else {
      log.info("The device is not provisioned. Unless the device is provisioned and upload file is resigned it can't used");
    }
    return versions;
  }

  public UploadVersion create(String versionName, Long uploadId, MultipartFile uploadedMultipartFile, UploadType type, Upload upload) throws TestsigmaException {
    UploadVersion uploadVersion = new UploadVersion();
    uploadVersion.setUploadId(uploadId);
    uploadVersion.setName(versionName);
    uploadVersion.setUploadType(type);
    uploadVersion.setUpload(upload);
    File uploadedFile = copyUploadToTempFile(uploadedMultipartFile);
    uploadVersion.setFileSize(uploadedMultipartFile.getSize());
    uploadVersion.setFileName(ObjectUtils.defaultIfNull(uploadedMultipartFile.getOriginalFilename(), "tmp")
      .replaceAll("\\s+", "_"));
    uploadVersion = this.uploadVersionRepository.save(uploadVersion);
    uploadFile(uploadedFile, uploadVersion);
    this.uploadVersionRepository.save(uploadVersion);
    return uploadVersion;
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

  public void resignTheUpload(UploadVersion version) {
    if (version.getUploadType() == UploadType.IPA) {
      ReSignTask reSignTask = new ReSignTask(webApplicationContext, null, version);
      ReSignTaskFactory.getInstance().startTask(reSignTask);
    } else {
      log.info(String.format("Upload Type - [%s]. Skipping iOS app resign upload task...", version.getUploadType()));
    }
  }

  public void publishEvent(UploadVersion version, EventType eventType) {
    UploadVersionEvent<UploadVersion> event = createEvent(version, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public UploadVersionEvent<UploadVersion> createEvent(UploadVersion version, EventType eventType) {
    UploadVersionEvent<UploadVersion> event = new UploadVersionEvent<>();
    event.setEventData(version);
    event.setEventType(eventType);
    return event;
  }
  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsUploadsEnabled()) return;
    log.debug("backup process for upload initiated");
    writeXML("upload_version", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for upload completed");
  }

  public Specification<UploadVersion> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    WorkspaceVersion applicationVersion = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("workspaceId", SearchOperation.EQUALITY, applicationVersion.getWorkspaceId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    UploadVersionSpecificationsBuilder uploadSpecificationsBuilder = new UploadVersionSpecificationsBuilder();
    uploadSpecificationsBuilder.params = params;
    return uploadSpecificationsBuilder.build();
  }

  @Override
  public Page<UploadVersion> findAll(Specification<UploadVersion> specification, Pageable pageRequest) {
    return this.uploadVersionRepository.findAll(specification, pageRequest);
  }

  @Override
  protected List<UploadVersionXMLDTO> mapToXMLDTOList(List<UploadVersion> list) {
    return mapper.mapUploads(list, this, null);
  }

  @Override
  protected List<UploadVersionXMLDTO> mapToXMLDTOList(List<UploadVersion> list, BackupDTO backupDTO) {
    return mapper.mapUploads(list, this, backupDTO.getSrcFiles());
  }

  public void delete(UploadVersion version) {
    this.uploadVersionRepository.delete(version);
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsUploadsEnabled()) return;
    log.debug("import process for upload versions initiated");
    importFiles("upload_version", importDTO);
    log.debug("import process for upload versions completed");
  }

  @Override
  public List<UploadVersion> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
    List<UploadVersionXMLDTO> uploads = xmlMapper.readValue(xmlData, new TypeReference<>() {
    });
    if (!hasToSkip(null, importDTO)) {
      return mapper.mapUploadVersionsList(uploads);
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public boolean hasToSkip(UploadVersion upload, BackupDTO importDTO) {
    return !importDTO.getIsSameApplicationType();
  }

  @Override
  void updateImportedId(UploadVersion upload, UploadVersion previous, BackupDTO importDTO) {
    previous.setImportedId(upload.getId());
    save(previous);
  }

  @Override
  UploadVersion processAfterSave(Optional<UploadVersion> previous, UploadVersion present, UploadVersion toImport, BackupDTO importDTO) {
    try {
      String originalFileName = ObjectUtils.defaultIfNull(present.getFileName(), "tmp")
              .replaceAll("\\s+", "_");
      String downloadPath = Files.createTempDirectory(present.getFileName()).toFile().getAbsolutePath() + "/" + originalFileName;
      client.downloadRedirectFile(present.getDownloadURL(), downloadPath, new HashMap<>());
      uploadFile(new File(downloadPath), present);
      this.updateUploadWithLatestUploadVersion(present, present.getUploadId());
    } catch (IOException | TestsigmaException e) {
      log.error("Failed to upload file", e.getMessage(), e);
    }
    return present;
  }

  @Override
  public Optional<UploadVersion> findImportedEntity(UploadVersion version, BackupDTO importDTO) {
    return uploadVersionRepository.findAllByUploadIdAndImportedId(version.getImportedId(), version.getId());
  }

  @Override
  public UploadVersion processBeforeSave(Optional<UploadVersion> previous, UploadVersion present, UploadVersion toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    Upload importedUpload = uploadService.findByImportedIdAndWorkspaceId(present.getUploadId(),importDTO.getWorkspaceId());
    present.setUploadId(importedUpload.getId());
    present.setUpload(importedUpload);
    return present;
  }


  @Override
  public UploadVersion copyTo(UploadVersion version) {
    return mapper.copyVersion(version);
  }

  @Override
  public UploadVersion save(UploadVersion version) {
    return uploadVersionRepository.save(version);
  }

  @Override
  public Optional<UploadVersion> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    // NEED TO CHECK WITH CHANDRA ONCE HOW BEST WE CAN HANDLE THIS
//    Long importedId = ids[0];
//    return uploadVersionRepository.findAllByApplicationIdAndImportedId(importDTO.getApplicationId(), importedId);
    return Optional.empty();
  }


  @Override
  public Optional<UploadVersion> findImportedEntityHavingSameName(Optional<UploadVersion> previous, UploadVersion current, BackupDTO importDTO) throws ResourceNotFoundException {
    return uploadVersionRepository.findByNameAndUploadId(current.getName(), current.getImportedId());
  }

  @Override
  public boolean hasImportedId(Optional<UploadVersion> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  @Override
  public boolean isEntityAlreadyImported(Optional<UploadVersion> previous, UploadVersion current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }


}
