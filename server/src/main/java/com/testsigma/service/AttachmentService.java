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
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.dto.export.AttachmentCloudXMLDTO;
import com.testsigma.dto.export.TestDataCloudXMLDTO;
import com.testsigma.dto.export.TestDataXMLDTO;
import com.testsigma.model.*;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.AttachmentXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AttachmentMapper;
import com.testsigma.repository.AttachmentRepository;
import com.testsigma.specification.AttachmentSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.util.HttpClient;
import com.testsigma.web.request.AttachmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttachmentService extends XMLExportImportService<Attachment> {
  private final AttachmentRepository attachmentRepository;
  private final StorageServiceFactory storageServiceFactory;
  private final AttachmentMapper attachmentMapper;
  private final AttachmentMapper mapper;
  private final HttpClient client;
  private final JWTTokenService jwtTokenService;

  public List<Attachment> findAllByEntityIdAndEntity(Long entityId, String entity, Pageable page) {
    Page<Attachment> list = attachmentRepository
      .findAllByEntityIdAndEntity(entityId, entity, page);

    List<Attachment> attachmentsList = list.getContent();

    for (Attachment attachment : attachmentsList) {
      String s3Key = "/attachments/" + attachment.getPath();
      Optional<URL> newS3URL = storageServiceFactory.getStorageService().generatePreSignedURLIfExists(s3Key, StorageAccessLevel.READ);
      if (newS3URL != null && newS3URL.isPresent()) {
        attachment.setPreSignedURL(newS3URL.get().toString());
      }
    }
    return attachmentsList;
  }

  public Page<Attachment> findAll(Specification<Attachment> spec, Pageable pageable) {
    return this.attachmentRepository.findAll(spec, pageable);
  }

  public Attachment find(Long id) throws ResourceNotFoundException {
    Attachment attachment = this.attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attachment missing"));
    String s3Key = "/attachments/" + attachment.getPath();
    Optional<URL> newS3URL = storageServiceFactory.getStorageService().generatePreSignedURLIfExists(s3Key, StorageAccessLevel.READ);
    if (newS3URL != null && newS3URL.isPresent())
      attachment.setPreSignedURL(newS3URL.get().toString());
    return attachment;
  }

  public void getPreSignedURL(Attachment attachment) {
    String s3Key = "/attachments/" + attachment.getPath();
    Optional<URL> newS3URL = storageServiceFactory.getStorageService().generatePreSignedURLIfExists(s3Key, StorageAccessLevel.READ);
    if (newS3URL != null && newS3URL.isPresent())
      attachment.setPreSignedURL(newS3URL.get().toString());
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    Attachment attachment = this.find(id);
    String s3Key = "/attachments/" + attachment.getPath();
    storageServiceFactory.getStorageService().deleteFile(s3Key);
    this.attachmentRepository.delete(attachment);
  }

  public Attachment create(AttachmentRequest attachmentRequest) throws IOException {
    MultipartFile fileContent = attachmentRequest.getFileContent();
    String originalFileName = Objects.requireNonNull(fileContent.getOriginalFilename()).replaceAll("\\s+", "_");
    StringBuffer path = new StringBuffer(attachmentRequest.getEntity().replaceAll("_", "-")).append(File.separator)
      .append(attachmentRequest.getEntityId()).append(File.separator).append(originalFileName);
    String s3Key = "/attachments/" + attachmentRequest.getEntity().replaceAll("_", "-") + "/" + attachmentRequest.getEntityId() + "/" + originalFileName;
    InputStream myInputStream = new ByteArrayInputStream(fileContent.getBytes());
    storageServiceFactory.getStorageService().addFile(s3Key, myInputStream);
    Attachment attachment = attachmentMapper.map(attachmentRequest);
    attachment.setPath(path.toString());
    return this.attachmentRepository.save(attachment);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsAttachmentEnabled()) return;
    log.debug("backup process for attachment initiated");
    backupDTO.setEntity(WorkspaceVersion.class.getName());
    writeXML("attachment_version", backupDTO, PageRequest.of(0, 25));
    writeXML("attachment_requirement", backupDTO, PageRequest.of(0, 25));
    backupDTO.setEntity(TestCase.class.getName());
    writeXML("attachment_test_case", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for attachment completed");
  }

  @Override
  protected List<AttachmentXMLDTO> mapToXMLDTOList(List<Attachment> list, BackupDTO backupDTO) {
    return mapper.mapAttachments(list, this, backupDTO.getSrcFiles());
  }

  @Override
  protected List<AttachmentXMLDTO> mapToXMLDTOList(List<Attachment> list) {
    return mapper.mapAttachments(list, this, null);
  }

  public Specification<Attachment> getExportXmlSpecification(BackupDTO backupDTO) {
    SearchCriteria criteria = new SearchCriteria("entity", SearchOperation.EQUALITY, backupDTO.getEntity());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    AttachmentSpecificationsBuilder attachmentSpecificationsBuilder = new AttachmentSpecificationsBuilder();
    attachmentSpecificationsBuilder.params = params;
    return attachmentSpecificationsBuilder.build();
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsAttachmentEnabled()) return;
    log.debug("backup process for attachment initiated");
    importDTO.setEntity(WorkspaceVersion.class.getName());
    importFiles("attachment_version", importDTO);
    importFiles("attachment_requirement", importDTO);
    importDTO.setEntity(TestCase.class.getName());
    importFiles("attachment_test_case", importDTO);
    log.debug("import process for attachment completed");
  }

  @Override
  public List<Attachment> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapCloudAttachmentsList(xmlMapper.readValue(xmlData, new TypeReference<List<AttachmentCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapAttachmentsList(xmlMapper.readValue(xmlData, new TypeReference<List<AttachmentXMLDTO>>() {
      }));
    }
  }


  @Override
  public Optional<Attachment> findImportedEntity(Attachment attachment, BackupDTO importDTO) {
    Optional<Attachment> previous = attachmentRepository.findByEntityIdAndEntityAndImportedId(attachment.getEntityId(), attachment.getEntity(), attachment.getId());
    return previous;
  }

  @Override
  public Attachment processBeforeSave(Optional<Attachment> previous, Attachment present, Attachment toImport, BackupDTO importDTO) {
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    present.setImportedId(toImport.getId());
    return present;
  }

  @Override
  Attachment processAfterSave(Optional<Attachment> previous, Attachment present, Attachment importEntity, BackupDTO importDTO) {
    return saveAttachment(present);
  }

  public Attachment saveAttachment(Attachment attachment) {
    try {
      String originalFileName = ObjectUtils.defaultIfNull(attachment.getName(), "tmp")
              .replaceAll("\\s+", "_");
      String downloadPath = Files.createTempDirectory(attachment.getName()).toFile().getAbsolutePath() + "/" + originalFileName;
      client.downloadRedirectFile(attachment.getPreSignedURL(), downloadPath, new HashMap<>());
      StringBuffer path = new StringBuffer(attachment.getEntity().replaceAll("_", "-")).append(File.separator)
              .append(attachment.getEntityId()).append(File.separator).append(originalFileName);
      String s3Key = "/attachments/" + attachment.getEntity().replaceAll("_", "-") + "/" + attachment.getEntityId() + "/" + originalFileName;
      InputStream myInputStream = new ByteArrayInputStream(new FileInputStream(downloadPath).readAllBytes());
      storageServiceFactory.getStorageService().addFile(s3Key, myInputStream);
      attachment.setPath(path.toString());
      return this.attachmentRepository.save(attachment);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return attachment;
  }

  @Override
  public Attachment copyTo(Attachment attachment) {
    return mapper.copy(attachment);
  }


  public Attachment save(Attachment attachment) {
    return attachmentRepository.save(attachment);
  }

  @Override
  public Optional<Attachment> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    Long entityId = ids[1];
    return attachmentRepository.findByEntityIdAndEntityAndImportedId(entityId, null, importedId);
  }

  public Optional<Attachment> findImportedEntityHavingSameName(Optional<Attachment> previous, Attachment current, BackupDTO importDTO) {
    List<Attachment> oldEntity = attachmentRepository.findAllByName(current.getName());
    if (oldEntity.size() > 0) {
      return Optional.of(oldEntity.get(0));
    } else {
      return Optional.empty();
    }
  }

  public boolean hasImportedId(Optional<Attachment> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<Attachment> previous, Attachment current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(Attachment attachment, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(Attachment attachment, Attachment previous, BackupDTO importDTO) {
    previous.setImportedId(attachment.getId());
    save(previous);
  }

}
