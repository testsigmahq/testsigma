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
import com.testsigma.dto.export.AttachmentXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AttachmentMapper;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.Attachment;
import com.testsigma.model.TestCase;
import com.testsigma.repository.AttachmentRepository;
import com.testsigma.specification.AttachmentSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.web.request.AttachmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttachmentService extends XMLExportService<Attachment> {
  private final AttachmentRepository attachmentRepository;
  private final StorageServiceFactory storageServiceFactory;
  private final AttachmentMapper attachmentMapper;
  private final AttachmentMapper mapper;

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
    String s3Key = "attachments/" + attachmentRequest.getEntity().replaceAll("_", "-") + "/" + attachmentRequest.getEntityId() + "/" + originalFileName;
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
}
