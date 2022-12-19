package com.testsigma.service.testproject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.testsigma.constants.MessageConstants;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.BackupActionType;
import com.testsigma.model.BackupDetail;
import com.testsigma.model.BackupStatus;
import com.testsigma.service.BackupDetailService;
import com.testsigma.service.ZipFileService;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestProjectImportService {

    private final ProjectImportService projectImportService;
    private final ZipFileService zipFileService;

    public void yamlImport(MultipartFile file) throws IOException, ResourceNotFoundException, TestProjectImportException {
        File tempFile = copyUploadToTempFile(file);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(StringUtils.containsIgnoreCase(extension, "zip")) {
            importFromZip(tempFile);
        } else if(StringUtils.containsIgnoreCase(extension,"yaml")) {
            importFromYamlFile(tempFile);
        }
    }

    private void importFromYamlFile(File yamlFile) throws ResourceNotFoundException, TestProjectImportException, IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            TestProjectYamlRequest testProjectYamlRequest = mapper.readValue(yamlFile, TestProjectYamlRequest.class);
            projectImportService.importFromRequest(testProjectYamlRequest);
        } catch (NoSuchMethodError e) {
            log.error("Could not parse value from yaml file: " + yamlFile + " to TestProjectYamlRequest");
        }
    }

    public void importFromZip(File zipFile) throws TestProjectImportException {
        try {
            File tempFolder = Files.createTempDirectory("test_project").toFile();
            File extractedFolder = zipFileService.unZipFile(zipFile, tempFolder);
            FileFilter fileFilter = new WildcardFileFilter("*.yaml");
            File[] files = extractedFolder.listFiles(fileFilter);
            if(files != null) {
                for (File file : files) {
                    importFromYamlFile(file);
                }
            }

        } catch (IOException | ResourceNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new TestProjectImportException("Error while importing from Zip File - " + e.getMessage());
        } catch (TestProjectImportException e) {
            e.printStackTrace();
        }
    }

    private File copyUploadToTempFile(MultipartFile uploadedFile) throws IOException {
        String fileName = uploadedFile.getOriginalFilename().replaceAll("\\s+", "_");
        String fileBaseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isNotBlank(extension)) {
            extension = "." + extension;
        }
        File tempFile = File.createTempFile(fileBaseName + "_", extension);
        log.info("Transferring uploaded multipart file to - " + tempFile.getAbsolutePath());
        uploadedFile.transferTo(tempFile);
        return tempFile;

    }
}
