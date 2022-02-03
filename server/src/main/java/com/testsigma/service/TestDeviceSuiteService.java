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
}
