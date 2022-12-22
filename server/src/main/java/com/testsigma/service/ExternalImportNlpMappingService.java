package com.testsigma.service;

import com.testsigma.model.ExternalImportNlpMapping;
import com.testsigma.model.ExternalImportType;
import com.testsigma.model.WorkspaceType;
import com.testsigma.repository.ExternalImportNlpMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ExternalImportNlpMappingService {

    private final ExternalImportNlpMappingRepository repository;


    public Optional<ExternalImportNlpMapping> findByExternalIdAndExternalImportTypeAndWorkspaceType(String externalId,
                                                                                                      ExternalImportType externalImportType,
                                                                                                      WorkspaceType workspaceType){
        return repository.findByExternalNlpIdAndAndExternalImportTypeAndWorkspaceType(externalId, externalImportType, workspaceType);
    }

}
