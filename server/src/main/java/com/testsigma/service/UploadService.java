/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.UploadCloudXMLDTO;
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
import com.testsigma.web.request.UploadRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class UploadService extends XMLExportImportService<Upload> {
  private final UploadRepository uploadRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final WorkspaceService workspaceService;
  private final WorkspaceVersionService workspaceVersionService;
  private final UploadVersionService uploadVersionService;
  private final UploadMapper mapper;

  public Upload find(Long id) throws ResourceNotFoundException {
    Upload upload = this.uploadRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Couldn't find upload version with " +
        "id: " + id));
    try{
    upload.getLatestVersion().setPreSignedURL(uploadVersionService.getPreSignedURL(upload.getLatestVersion()));
    }
    catch (Exception e){
      log.error(e.getMessage());
    }
  return upload;
  }

  public Page<Upload> findAll(Specification<Upload> specification, Pageable pageable) {
    return uploadRepository.findAll(specification, pageable);
  }

  public Upload findById(Long id) {
    return this.uploadRepository.findById(id).orElse(null);
  }

  public Upload create(UploadRequest uploadRequest) throws TestsigmaException {
    Upload upload = new Upload();
    upload.setName(uploadRequest.getName());
    upload.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    upload.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    upload.setWorkspaceId(uploadRequest.getWorkspaceId());
    upload.setSupportedDeviceType(uploadRequest.getSupportedDeviceType());
    upload = this.save(upload);
    upload.setWorkspace(this.workspaceService.find(uploadRequest.getWorkspaceId()));
    UploadVersion version = uploadVersionService.create(uploadRequest.getVersion(), upload.getId(), uploadRequest.getFileContent(), uploadRequest.getUploadType(), upload);
    upload.setLatestVersionId(version.getId());
    upload.setLatestVersion(version);
    publishEvent(upload, EventType.CREATE);
    return this.save(upload);
  }

  public Upload update(Upload upload, UploadRequest uploadRequest) throws TestsigmaException {
    upload.setName(uploadRequest.getName());
    upload.setSupportedDeviceType(uploadRequest.getSupportedDeviceType());
    upload.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    if(!(uploadRequest.getFileContent() == null || (uploadRequest.getFileContent() != null && "".equals(uploadRequest.getFileContent().getOriginalFilename())))) {
      UploadVersion version = uploadVersionService.create(uploadRequest.getVersion(), upload.getId(), uploadRequest.getFileContent(), uploadRequest.getUploadType(), upload);
      upload.setLatestVersionId(version.getId());
    }
    return this.update(upload);

  }

  public Upload update(Upload upload) throws TestsigmaException {
    upload = this.uploadRepository.save(upload);
    publishEvent(upload, EventType.UPDATE);
    return upload;
  }

  public Upload save(Upload upload) {
    return uploadRepository.save(upload);
  }

  public void delete(Upload upload) {
    this.uploadRepository.delete(upload);
    publishEvent(upload, EventType.DELETE);
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
    return  mapper.mapUploads(list);
  }

  @Override
  protected List<UploadXMLDTO> mapToXMLDTOList(List<Upload> list, BackupDTO backupDTO) {
    return mapper.mapUploads(list);
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsUploadsEnabled()) return;
    log.debug("import process for uploads initiated");
    importFiles("uploads", importDTO);
    log.debug("import process for uploads completed");
  }

  @Override
  public List<Upload> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {

    if (importDTO.getIsCloudImport()) {
      if (!hasToSkip(null, importDTO)) {
        return mapper.mapUploadsCloudXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<UploadCloudXMLDTO>>() {
        }));
      } else {
        return new ArrayList<>();
      }
    } else {
      if (!hasToSkip(null, importDTO)) {
        return mapper.mapUploadsXMLList(xmlMapper.readValue(xmlData, new TypeReference<List<UploadXMLDTO>>() {
        }));
      } else {
        return new ArrayList<>();
      }
    }

  }

  @Override
  public boolean hasToSkip(Upload upload, BackupDTO importDTO) {
    return !importDTO.getIsSameApplicationType();
  }

  @Override
  void updateImportedId(Upload upload, Upload previous, BackupDTO importDTO) {
    previous.setImportedId(upload.getId());
    save(previous);
  }

  @Override
  public Optional<Upload> findImportedEntity(Upload upload, BackupDTO importDTO) {
    return uploadRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), upload.getId());
  }

  @Override
  public Upload processBeforeSave(Optional<Upload> previous, Upload present, Upload toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    present.setWorkspaceId(importDTO.getWorkspaceId());
    return present;
  }
  public List<Upload> findAllByApplicationId(Long applicationVersionId){
    return this.uploadRepository.findAllByWorkspaceId(applicationVersionId);
  }

  @Override
  public Upload copyTo(Upload upload) {
    return mapper.copy(upload);
  }


  @Override
  public Optional<Upload> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return uploadRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), importedId);
  }


  @Override
  public Optional<Upload> findImportedEntityHavingSameName(Optional<Upload> previous, Upload current, BackupDTO importDTO) throws ResourceNotFoundException {
    return uploadRepository.findByNameAndWorkspaceId(current.getName(), importDTO.getWorkspaceId());
  }

  @Override
  public boolean hasImportedId(Optional<Upload> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  @Override
  public boolean isEntityAlreadyImported(Optional<Upload> previous, Upload current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  public Optional<Upload> findByImportedIdAndWorkspaceId(Long importedId,
                                                         Long applicationId) {
    return uploadRepository.findByImportedIdAndWorkspaceId(importedId,applicationId);
  }

}
