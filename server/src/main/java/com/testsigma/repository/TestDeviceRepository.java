/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.TestDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
@Transactional
public interface TestDeviceRepository extends BaseRepository<TestDevice, Long> {
  List<TestDevice> findTestDeviceByAgentId(Long agentId);

  List<TestDevice> findByTestPlanIdAndDisable(Long testPlanId, Boolean disable);

  Page<TestDevice> findAll(Specification<TestDevice> spec, Pageable pageable);

  List<TestDevice> findByWorkspaceVersionWorkspaceIdAndAppUploadId(Long workspaceId, Long appUploadId);

  List<TestDevice> findByTestPlanId(Long testPlanId);

  @Modifying
  @Query("DELETE FROM TestDevice exeEnv WHERE exeEnv.id IN (:ids)")
  void deleteAllByIds(@Param("ids") Set<Long> ids);

  List<TestDevice> findAllByDeviceIdIn(List<Long> removedAgentDeviceIds);

  @Modifying
  @Query(value = "UPDATE test_devices td INNER JOIN test_plans tp ON td.test_plan_id = tp.id SET td.app_upload_id = NULL WHERE td.app_upload_id = :appUploadId and tp.entity_type = 'ADHOC_TEST_PLAN'", nativeQuery = true)
  void resentAppUploadIdToNull(@Param("appUploadId") Long appUploadId);


  @Modifying
  @Query(value = "UPDATE test_devices td INNER JOIN test_plans tp ON td.test_plan_id = tp.id SET td.agent_id = NULL WHERE td.agent_id = :agentId and tp.entity_type = 'ADHOC_TEST_PLAN'", nativeQuery = true)
  void resetAgentIdToNull(@Param("agentId") Long agentId);

  @Query("SELECT testDevice FROM TestDevice testDevice " +
          " LEFT JOIN TestDeviceSuite testDeviceSuite ON testDevice.id = testDeviceSuite.testDeviceId " +
          " WHERE testDeviceSuite.suiteId =:suiteId")
  List<TestDevice> findAllByTestSuiteId(Long suiteId);

    Optional<TestDevice> findAllByTestPlanIdAndImportedId(Long id, Long testPlanId);

  Optional<TestDevice> findAllByTestPlanIdInAndImportedId(List<Long> executionIds, Long importedId);

  Optional<TestDevice> findAllByTestPlanIdAndTitle(Long id, String title);
}
