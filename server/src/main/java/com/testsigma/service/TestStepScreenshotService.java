/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.TestStepScreenshot;
import com.testsigma.repository.TestStepScreenshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestStepScreenshotService {

  private final TestStepScreenshotRepository repository;

  public TestStepScreenshot find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Missing with id:" + id));
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestStepScreenshot testStepScreenshot = this.find(id);
    this.repository.delete(testStepScreenshot);
  }

  public TestStepScreenshot update(TestStepScreenshot stepScreenshot) {
    return this.repository.save(stepScreenshot);
  }

  public TestStepScreenshot create(TestStepScreenshot stepScreenshot) {
    return this.repository.save(stepScreenshot);
  }

  public Optional<TestStepScreenshot> findBaseScreenshotForMobile(Long stepId, String deviceName, String testDataSetName,
                                                                  Long testDataId, String imageSize,String entityType) {
    return this.repository.findBaseScreenshotForMobile(stepId, deviceName, testDataSetName, testDataId, imageSize,entityType);
  }

  public Optional<TestStepScreenshot> findBaseScreenshotForWeb(Long stepId, String deviceName, String browser, String resolution, String testDataSetName,
                                                               Long testDataId, String imageSize,String entityType) {
    return this.repository.findBaseScreenshotForWeb(stepId, deviceName, browser, resolution, testDataSetName, testDataId, imageSize,entityType);
  }
}

