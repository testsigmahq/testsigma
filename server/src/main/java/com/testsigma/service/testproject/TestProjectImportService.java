package com.testsigma.service.testproject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.util.ZipUtil;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestProjectImportService {

    private final ProjectImportService projectImportService;

    public void yamlImport(MultipartFile file) throws IOException, ResourceNotFoundException, TestProjectImportException {
        File tempFile = copyUploadToTempFile(file);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(StringUtils.containsIgnoreCase(extension, "zip")) {
            tempFile = importFromZip(tempFile);
            extension = FilenameUtils.getExtension(tempFile.getAbsolutePath());
        }
        if(StringUtils.containsIgnoreCase(extension,"yaml")) {
            importFromYamlFile(tempFile);
        }
    }

    private void importFromYamlFile(File yamlFile) throws ResourceNotFoundException, TestProjectImportException, IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TestProjectYamlRequest testProjectYamlRequest = mapper.readValue(yamlFile, TestProjectYamlRequest.class);
        projectImportService.importFromRequest(testProjectYamlRequest);
    }

    public File importFromZip(File zipFile) throws IOException {
        File destFolder = Files.createTempDirectory("test_project_" + zipFile.getName()).toFile();
        return ZipUtil.unZipFile(zipFile.getPath(), destFolder);
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
