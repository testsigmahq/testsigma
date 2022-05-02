/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Upload;
import com.testsigma.model.UploadType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UploadRepository extends BaseRepository<Upload, Long> {

    Optional<Upload> findAllByWorkspaceIdAndImportedId(Long applicationId, Long id);

    Optional<Upload> findByNameAndWorkspaceId(String name, Long applicationVersionId);

    List<Upload> findAllByWorkspaceId(Long applicationVersionId);
}
