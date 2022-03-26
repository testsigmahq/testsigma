package com.testsigma.service;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.constants.MessageConstants;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.BaseXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.util.ZipUtil;
import com.testsigma.model.BackupDetail;
import com.testsigma.model.BackupStatus;
import com.testsigma.model.Upload;
import com.testsigma.repository.BackupDetailRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;

@Service
@Log4j2
public abstract class XMLExportService<T> {
  @Getter
  protected File srcFiles = null;
  @Autowired
  protected StorageServiceFactory storageServiceFactory;
  @Getter
  private File destFiles = null;
  @Autowired
  private BackupDetailRepository backupDetailsRepository;
  @Autowired
  private ObjectMapperService objectMapperService;

  private String fileName = "backup.zip";

  public void initExportFolder(BackupDTO backupDTO) throws IOException {
    String path = System.getProperty("java.io.tmpdir");

    String sourcePtah = path + File.separator + "xmls" + File.separator
      + backupDTO.getId();
    String destPtah = path + File.separator + "zip" + File.separator
      + backupDTO.getId();

    File folder = new File(sourcePtah);
    if (!folder.isDirectory() && !folder.exists()) {
      folder.mkdirs();
    }
    backupDTO.setSrcFiles(folder);

    File dest = new File(destPtah);
    if (!dest.isDirectory() && !dest.exists()) {
      dest.mkdirs();
    }
    backupDTO.setDestFiles(dest);
  }

  protected void createFile(List list, BackupDTO backupDTO, String fileName, int index) throws IOException {
    if (list.size() == 0) return;
    log.debug("backup file " + backupDTO.getSrcFiles().getAbsolutePath() + File.separator + fileName + "_" + index + ".xml" + " initiated");
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    File fileToZip = new File(backupDTO.getSrcFiles().getAbsolutePath() + File.separator + fileName + "_" + index + ".xml");
    FileOutputStream fileOutputStream = new FileOutputStream(fileToZip);

    Class<T> cls = (Class<T>) list.get(0).getClass();
    ObjectWriter writer = xmlMapper.writer().withRootName(cls.getAnnotation(JsonListRootName.class).name());
    ObjectNode rootNode = xmlMapper.createObjectNode();
    ArrayNode dtoList = rootNode.putArray(cls.getAnnotation(JsonRootName.class).value());
    list.stream().forEach(entity -> {
      dtoList.addPOJO(entity);
    });
    fileOutputStream.write(writer.writeValueAsString(rootNode).getBytes());
    fileOutputStream.flush();
    fileOutputStream.close();
    log.debug("backup file " + backupDTO.getSrcFiles().getAbsolutePath() + File.separator + fileName + "_" + index + ".xml" + " completed");
  }

  public void writeXML(String fileName, BackupDTO backupDTO, Pageable pageable) throws IOException, ResourceNotFoundException {
    Specification<T> spec = getExportXmlSpecification(backupDTO);
    Page<T> page = findAll(spec, pageable);
    createFile(mapToXMLDTOList(page.getContent(), backupDTO), backupDTO, fileName, pageable.getPageNumber());
    if (!page.isLast()) {
      log.info("Processing export next page");
      Pageable nextPage = pageable.next();
      writeXML(fileName, backupDTO, nextPage);
    }
  }

  public void destroy(BackupDTO backupDTO) {
    try {
      if (backupDTO.getSrcFiles().exists())
        backupDTO.getSrcFiles().delete();

      if (backupDTO.getSrcFiles().exists())
        backupDTO.getSrcFiles().delete();
    } catch (Exception e) {
      log.error(e, e);
    }
  }

  public BackupDetail create(BackupDetail backupDetail) {
    createEntry(backupDetail);
    return backupDetail;
  }

  public void createEntry(BackupDetail backupDetail) {
    backupDetail.setMessage(MessageConstants.BACKUP_IS_IN_PROGRESS);
    backupDetail.setStatus(BackupStatus.IN_PROGRESS);
    backupDetail.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    this.backupDetailsRepository.save(backupDetail);
  }

  public void exportToStorage(BackupDetail backupDetail) throws IOException {
    File outputFile = null;
    try {
      String s3Key = "/backup/" + backupDetail.getName();
      log.debug("backup zip process initiated");
      outputFile = new ZipUtil().zipFile(backupDetail.getSrcFiles(), fileName, backupDetail.getDestFiles());
      log.debug("backup zip process completed");
      InputStream fileInputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(outputFile));
      storageServiceFactory.getStorageService().addFile(s3Key, fileInputStream);
      backupDetail.setStatus(BackupStatus.SUCCESS);
      backupDetail.setMessage(MessageConstants.BACKUP_IS_SUCCESS);
      backupDetailsRepository.save(backupDetail);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      try {
        backupDetail.setStatus(BackupStatus.FAILURE);
        backupDetail.setMessage(e.getMessage());
        backupDetailsRepository.save(backupDetail);
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    } finally {
      outputFile.delete();
    }
  }

  protected abstract Page<T> findAll(Specification<T> specification, Pageable pageRequest) throws ResourceNotFoundException;

  protected abstract List<? extends BaseXMLDTO> mapToXMLDTOList(List<T> list);

  public abstract Specification<T> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException;

  protected List<? extends BaseXMLDTO> mapToXMLDTOList(List<T> list, BackupDTO backupDTO) {
    if (list.size() > 0 && list.get(0) instanceof Upload) {
      return mapToXMLDTOList(list, backupDTO);
    }
    return mapToXMLDTOList(list);
  }
}
