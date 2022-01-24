/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestDeviceSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestDeviceSuiteRepository extends JpaRepository<TestDeviceSuite, Long> {

  Optional<TestDeviceSuite> findFirstByTestDeviceAndTestSuite(TestDevice testDevice,
                                                              AbstractTestSuite testSuite);

  List<TestDeviceSuite> findAllByTestDeviceIdOrderByPosition(Long environmentId);

  @Query("SELECT suiteMapping FROM TestDeviceSuite suiteMapping WHERE suiteMapping.suiteId IN (:suiteIds) and suiteMapping.testDeviceId = :testDeviceId ")
  List<TestDeviceSuite> findByTestDeviceIdAndSuiteIds(@Param("testDeviceId") Long testDeviceId, @Param("suiteIds") List<Long> suiteIds);
}
