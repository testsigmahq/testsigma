/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.ProvisioningProfileUpload;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProvisioningProfileUploadRepository extends BaseRepository<ProvisioningProfileUpload, Long> {

  List<ProvisioningProfileUpload> findAllByProvisioningProfileId(Long profileId);

  ProvisioningProfileUpload findByProvisioningProfileIdAndUploadVersionId(Long provisioningProfileId, Long uploadId);

  List<ProvisioningProfileUpload> findAllByUploadVersionId(Long uploadId);
}
