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
import com.testsigma.service.UploadVersionService;
import com.testsigma.service.ZipFileService;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
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
import java.util.Iterator;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestProjectImportService {

    private final ProjectImportService projectImportService;
    private final ZipFileService zipFileService;
    private final UploadVersionService uploadVersionService;

    public void yamlImport(MultipartFile file) throws IOException, TestsigmaException {
        File tempFile = uploadVersionService.copyUploadToTempFile(file);
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

    private boolean importFromYamlFile(File yamlFile) throws TestProjectImportException, IOException, ResourceNotFoundException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Optional<TestProjectYamlRequest> testProjectYamlRequest = Optional.empty();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            testProjectYamlRequest = Optional.of(mapper.readValue(yamlFile, TestProjectYamlRequest.class));
        } catch (Throwable e) {
            log.error("Could not parse value from yaml file: " + yamlFile.getName() + " to TestProjectYamlRequest");
        }
        if(testProjectYamlRequest.isPresent()) {
            projectImportService.importFromRequest(testProjectYamlRequest.get());
            return true;
        }
        return false;
    }

    public void importFromZip(File zipFile) throws TestProjectImportException {
        Boolean atleastOneFileImported = false, currentFileImported;
        try {
            File tempFolder = Files.createTempDirectory("test_project").toFile();
            File extractedFolder = zipFileService.unZipFile(zipFile, tempFolder);
            Iterator<File> files = FileUtils.iterateFiles(extractedFolder, null,true);
            while(files.hasNext()){
                File file = files.next();
                String extension = FilenameUtils.getExtension(file.getName());
                if(extension.isBlank() || extension.contains("yaml")){
                    log.info("Trying to import testproject yaml file: " + file.getName());
                    currentFileImported = importFromYamlFile(file);
                    atleastOneFileImported = !atleastOneFileImported ? currentFileImported : atleastOneFileImported;
                }
            }
            if(!atleastOneFileImported) {
                throw new TestProjectImportException("No yaml file imported successfully");
            }
        } catch (IOException | ResourceNotFoundException | TestProjectImportException e) {
            log.error(e.getMessage(), e);
            throw new TestProjectImportException("Error while importing from Zip File - " + e.getMessage());
        }
    }
}
