package com.testsigma.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.testsigma.event.EventType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.exception.UnZipException;
import com.testsigma.model.*;
import com.testsigma.util.ZipUtil;
import com.testsigma.repository.BackupDetailRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public abstract class XMLExportImportService<T> {
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

    public static void initImportFolder(BackupDTO importDTO, String unzipDir) throws IOException, UnZipException {
        File destFolder = Files.createTempDirectory("import_" + importDTO.getId()).toFile();
        File unZippedFolder = unZipFile(importDTO.getImportFileUrl(), destFolder, unzipDir);
        importDTO.setDestFiles(unZippedFolder);
    }

    public static void destroyImport(BackupDTO importDTO) {
        try {
            if (importDTO.getDestFiles().exists())
                importDTO.getDestFiles().delete();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    private static File unZipFile(String s3Path, File targetFolder, String unzipDir) throws IOException, UnZipException {
        File zipFile = File.createTempFile(System.currentTimeMillis() + "", ".zip");
        FileUtils.copyURLToFile(new URL(s3Path), zipFile);
        ProcessBuilder processBuilder = new ProcessBuilder(unzipDir + "unzip", zipFile.getAbsolutePath(), "-d", targetFolder.getAbsolutePath());
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process;
        try {
            process = processBuilder.start();
            process.waitFor();
            if (process.exitValue() != 0)
                throw new UnZipException();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UnZipException();
        } finally {
            zipFile.delete();
        }
        return targetFolder;
    }

    public void initExportFolder(BackupDTO backupDTO) throws IOException {
        String path = System.getProperty("java.io.tmpdir");

        String sourcePath = path + File.separator + "xmls" + File.separator
                + backupDTO.getName();
        String destPath = path + File.separator + "zip" + File.separator
                + backupDTO.getName();

        File folder = new File(sourcePath);
        if (!folder.isDirectory() && !folder.exists()) {
            folder.mkdirs();
        }
        backupDTO.setSrcFiles(folder);

        File dest = new File(destPath);
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

    public File copyZipFileToTemp(MultipartFile uploadedFile) throws TestsigmaException {
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

    public void uploadImportFileToStorage(File uploadedFile, BackupDetail backupDetail) throws TestsigmaException {

        try {
            String originalFileName = ObjectUtils.defaultIfNull(uploadedFile.getName(), "tmp")
                    .replaceAll("\\s+", "_");
            StringBuilder storageFilePath =
                    new StringBuilder().append("/backup/").append(backupDetail.getName());
            log.info(String.format("Uploading import file:%s to storage path %s", uploadedFile.getAbsolutePath(), storageFilePath));
            InputStream fileInputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(uploadedFile));
            storageServiceFactory.getStorageService().addFile(storageFilePath.toString(), fileInputStream);
            backupDetail.setStatus(BackupStatus.IN_PROGRESS);
            backupDetail.setMessage(MessageConstants.IMPORT_IS_IN_PROGRESS);
            this.backupDetailsRepository.save(backupDetail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                backupDetail.setStatus(BackupStatus.FAILURE);
                backupDetail.setMessage(e.getMessage());
                this.backupDetailsRepository.save(backupDetail);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    void importFile(BackupDTO importDTO, File file) throws IOException, ResourceNotFoundException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String data = FileUtils.readFileToString(file, "UTF-8");
        List<T> list = readEntityListFromXmlData(data, xmlMapper, importDTO);
        for (T entity : list) {
            saveEntityFromXML(entity, importDTO);
        }
    }


    void saveEntityFromXML(T entity, BackupDTO importDTO) throws ResourceNotFoundException {
        Optional<T> previous = findImportedEntity(entity, importDTO);
        T importEntity = copyTo(entity);
        if (!hasImportedId(previous)) {
            previous = findImportedEntityHavingSameName(previous, entity, importDTO);
            if (previous.isPresent() && !importDTO.getSkipEntityExists()) {
                importDTO.setHasToReset(true);
                save(previous, entity, importDTO, importEntity);
            } else if (previous.isPresent() && importDTO.getSkipEntityExists()) {
                //skip
                log.debug("entity already present so skipping process updating only ImportedId");
                updateImportedId(entity, previous.get(), importDTO);
                log.debug("entity already present so skipping process updated only ImportedId");
            } else {
                save(previous, entity, importDTO, importEntity);
            }
        } else if (isEntityAlreadyImported(previous, entity)) {
            Optional<T> previousWithSameName = findImportedEntityHavingSameName(previous, entity, importDTO);
            if (previousWithSameName.isPresent()) {
                previous = previousWithSameName;
            }
            if (previous.isPresent() && !importDTO.getSkipEntityExists()) {
                importDTO.setHasToReset(true);
                save(previous, entity, importDTO, importEntity);
            }
        } else {
            Optional<T> previousWithSameName = findImportedEntityHavingSameName(previous, entity, importDTO);
            if (previousWithSameName.isPresent()) {
                log.debug("entity already present so skipping process updating only ImportedId");
                updateImportedId(entity, previousWithSameName.get(), importDTO);
                log.debug("entity already present so skipping process updated only ImportedId");
            }
        }
    }

    void save(Optional<T> previous, T entity, BackupDTO importDTO, T importEntity) throws ResourceNotFoundException {

        if (!hasToSkip(entity, importDTO)) {
            log.debug("before saving entity");
            processBeforeSave(previous, entity, importEntity, importDTO);
            T saved = save(entity);
            log.debug("process after saving entity");
            processAfterSave(previous, saved, importEntity, importDTO);
            log.debug("After saving entity");
        }
    }

    void importFiles(String fileName, BackupDTO importDTO) throws IOException, ResourceNotFoundException {
        FileFilter fileFilter = new RegexFileFilter("^" + fileName + "_\\d+.xml$");
        File[] files = importDTO.getDestFiles().listFiles(fileFilter);
        for (File file : files) {
            importFile(importDTO, file);
        }
    }

    abstract List<T> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException;

    abstract Optional<T> findImportedEntity(T t, BackupDTO importDTO);

    abstract Optional<T> findImportedEntityHavingSameName(Optional<T> previous, T t, BackupDTO importDTO) throws ResourceNotFoundException;

    abstract boolean hasImportedId(Optional<T> previous);

    abstract boolean isEntityAlreadyImported(Optional<T> previous, T t);

    abstract T processBeforeSave(Optional<T> previous, T present, T importEntity, BackupDTO importDTO) throws ResourceNotFoundException;

    abstract T copyTo(T t);

    abstract T save(T t);

    abstract Optional<T> getRecentImportedEntity(BackupDTO importDTO, Long... ids);

    abstract boolean hasToSkip(T t, BackupDTO importDTO);

    abstract void updateImportedId(T t, T previous, BackupDTO importDTO);

    T processAfterSave(Optional<T> previous, T present, T importEntity, BackupDTO importDTO) {
        return present;
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
