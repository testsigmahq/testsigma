/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.ElementScreenName;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ElementScreenNameRepository extends BaseRepository<ElementScreenName, Long> {
  Optional<ElementScreenName> findByNameAndWorkspaceVersionId(String name, Long workspaceVersionId);
  Optional<ElementScreenName> findAllByWorkspaceVersionIdAndImportedId(Long applicationVersionId, Long id);

}

