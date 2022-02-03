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

@Repository
@Transactional
public interface UploadRepository extends BaseRepository<Upload, Long> {
  List<Upload> findAllByType(UploadType type);
}
