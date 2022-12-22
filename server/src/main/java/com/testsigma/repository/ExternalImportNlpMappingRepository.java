package com.testsigma.repository;

import com.testsigma.model.ExternalImportNlpMapping;
import com.testsigma.model.ExternalImportType;
import com.testsigma.model.WorkspaceType;

import java.util.Optional;

public interface ExternalImportNlpMappingRepository extends BaseRepository<ExternalImportNlpMapping, Long> {

    Optional<ExternalImportNlpMapping> findByExternalNlpIdAndAndExternalImportTypeAndWorkspaceType(String externalNlpId,
                                                                                                   ExternalImportType type,
                                                                                                   WorkspaceType applicationType);


}
