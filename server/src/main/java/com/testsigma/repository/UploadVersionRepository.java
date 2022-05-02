/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.UploadType;
import com.testsigma.model.UploadVersion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UploadVersionRepository extends BaseRepository<UploadVersion, Long> {
  List<UploadVersion> findAllByLastUploadedTimeBeforeAndUploadTypeIn(Timestamp lastUploadedTime, Collection<UploadType> uploadType);

  List<UploadVersion> findAllByUploadTypeIn(Collection<UploadType> uploadType);


  Optional<UploadVersion> findByNameAndUploadId(String name, Long importedId);

    Optional<UploadVersion> findAllByUploadIdAndImportedId(Long importedId, Long id);
}
