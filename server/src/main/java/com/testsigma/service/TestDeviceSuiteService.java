/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestDeviceSuite;
import com.testsigma.model.TestSuite;
import com.testsigma.repository.TestDeviceSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDeviceSuiteService {

  private final TestDeviceSuiteRepository testDeviceSuiteRepository;
  private final TestDeviceService testDeviceService;

  public Optional<TestDeviceSuite> findFirstByTestDeviceAndTestSuite(
    TestDevice testDevice, AbstractTestSuite testSuite) {
    return testDeviceSuiteRepository.findFirstByTestDeviceAndTestSuite(testDevice,
      testSuite);
  }


  public List<TestDeviceSuite> findAllByTestDeviceId(Long environmentId) {
    return this.testDeviceSuiteRepository.findAllByTestDeviceIdOrderByPosition(environmentId);
  }


  public TestDeviceSuite add(TestDeviceSuite testDeviceSuite) {
    return this.testDeviceSuiteRepository.save(testDeviceSuite);
  }

  public TestDeviceSuite update(TestDeviceSuite testDeviceSuite) {
    return this.testDeviceSuiteRepository.save(testDeviceSuite);
  }

  public Boolean deleteAll(List<TestDeviceSuite> deletableMaps) {
    this.testDeviceSuiteRepository.deleteAll(deletableMaps);
    return true;
  }

  public void handlePreRequisiteChange(TestSuite testSuite) {
    List<TestDevice> executionEnvironments = this.testDeviceService.findAllByTestSuiteId(testSuite.getId());
    executionEnvironments.forEach(testDevice -> {
      List<Long> suiteIds = testDeviceSuiteRepository.findSuiteIdsByTestDeviceId(testDevice.getId());
      if (!suiteIds.contains(testSuite.getPreRequisite())) {
        int indexOfSuiteId = suiteIds.indexOf(testSuite.getId());
        suiteIds.add(indexOfSuiteId, testSuite.getPreRequisite());
        testDevice.setSuiteIds(suiteIds);
        testDeviceService.handleEnvironmentSuiteMappings(testDevice);
      }
    });
  }
}
