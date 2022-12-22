package com.testsigma.controller;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.service.testproject.ProjectImportService;
import com.testsigma.web.request.YamlImportRequest;
import com.testsigma.web.request.testproject.TestProjectYamlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Log4j2
@RequestMapping(path = "/local/test_project")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestProjectImportController {

    private final ProjectImportService projectImportService;

    @PostMapping(value = "/yaml")
    public void importYaml(@ModelAttribute YamlImportRequest yamlImportRequest) throws IOException, ResourceNotFoundException, TestProjectImportException {
        File tempFile = copyUploadToTempFile(yamlImportRequest.getMultipartFile());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TestProjectYamlRequest testProjectYamlRequest = mapper.readValue(tempFile, TestProjectYamlRequest.class);
        log.info(testProjectYamlRequest);
        projectImportService.importFromRequest(testProjectYamlRequest);
    }

    private File copyUploadToTempFile(MultipartFile uploadedFile) throws IOException {

        String fileName = uploadedFile.getOriginalFilename().replaceAll("\\s+", "_");
        String fileBaseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isNotBlank(extension)) {
            extension = "." + extension;
        }
        File tempFile = null;
        tempFile = File.createTempFile(fileBaseName + "_", extension);
        log.info("Transferring uploaded multipart file to - " + tempFile.getAbsolutePath());
        uploadedFile.transferTo(tempFile);
        return tempFile;

    }

}
