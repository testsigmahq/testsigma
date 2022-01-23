/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.ProvisioningProfileDevice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface ProvisioningProfileDeviceRepository extends BaseRepository<ProvisioningProfileDevice, Long> {
  List<ProvisioningProfileDevice> findAllByDeviceUDIdIn(Collection<String> deviceUDId);

  List<ProvisioningProfileDevice> findAllByProvisioningProfileId(Long provisioningProfileId);

  List<ProvisioningProfileDevice> findAllByDeviceUDIdInAndProvisioningProfileIdNot(Collection<String> deviceUDId, Long provisioningProfileId);

  ProvisioningProfileDevice findFirstByAgentDeviceId(Long deviceId);

  List<ProvisioningProfileDevice> findByDeviceUDId(String uniqueId);

  ProvisioningProfileDevice findByDeviceUDIdAndProvisioningProfileIdAndAgentDeviceIdIsNull(String uniqueId,
                                                                                           Long provisioningProfileId);

  ProvisioningProfileDevice findByDeviceUDIdAndAgentDeviceIdAndProvisioningProfileId(String uniqueId, Long agentDeviceId,
                                                                                     Long provisioningProfileId);
}
