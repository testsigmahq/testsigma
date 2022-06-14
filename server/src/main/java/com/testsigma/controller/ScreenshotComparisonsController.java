/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.dto.StepResultScreenshotComparisonDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.StepResultScreenshotComparisonMapper;
import com.testsigma.model.StepResultScreenshotComparison;
import com.testsigma.model.TestStepResult;
import com.testsigma.model.TestStepScreenshot;
import com.testsigma.service.StepResultScreenshotComparisonService;
import com.testsigma.service.TestStepScreenshotService;
import com.testsigma.specification.ScreenshotComparisionSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@RequestMapping(path = "/screenshot_comparisons", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ScreenshotComparisonsController {
  private final TestStepScreenshotService testStepScreenshotService;
  private final StepResultScreenshotComparisonService service;
  private final StepResultScreenshotComparisonMapper mapper;
  private final StorageServiceFactory storageServiceFactory;

  @GetMapping
  public Page<StepResultScreenshotComparisonDTO> index(ScreenshotComparisionSpecificationsBuilder builder, Pageable pageable) {
    log.info("Request /screenshot_comparisons/");
    Specification<StepResultScreenshotComparison> spec = builder.build();
    Page<StepResultScreenshotComparison> comparisons = service.findAll(spec, pageable);
    List<StepResultScreenshotComparisonDTO> testStepResultDTOS =
      mapper.map(comparisons.getContent());
    return new PageImpl<>(testStepResultDTOS, pageable, comparisons.getTotalElements());
  }

  @GetMapping("/{id}")
  public StepResultScreenshotComparisonDTO show(@PathVariable(value = "id") Long id) throws Exception {
    log.info("Request /screenshot_comparisons/" + id);
    StepResultScreenshotComparison comparison = service.find(id);
    TestStepResult testStepResult = comparison.getTestStepResult();
    Long screenShotPathId = testStepResult.getTestCaseResultId();
    String currentScreenShotPath =
      "/executions/" + screenShotPathId + "/" + testStepResult.getScreenshotName();
    URL url = storageServiceFactory.getStorageService().generatePreSignedURL(currentScreenShotPath, StorageAccessLevel.READ);
    comparison.setScreenShotURL(url.toString());

    TestStepResult baseTestStepResult = comparison.getTestStepScreenshot().getTestStepResult();
    Long baseScreenShotPathId = baseTestStepResult.getTestCaseResultId();
    String baseScreenShotPath =
      "/executions/" + baseScreenShotPathId + "/" + baseTestStepResult.getScreenshotName();
    url = storageServiceFactory.getStorageService().generatePreSignedURL(baseScreenShotPath, StorageAccessLevel.READ);
    comparison.getTestStepScreenshot().setScreenShotURL(url.toString());
    return mapper.map(comparison);
  }

  @PutMapping("/{id}/mark_as_base")
  public StepResultScreenshotComparisonDTO markAsBaseLine(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Request /screenshot_comparisons/" + id);
    StepResultScreenshotComparison comparison = service.find(id);
    TestStepResult testStepResult = comparison.getTestStepResult();
    TestStepScreenshot testStepScreenshot = comparison.getTestStepScreenshot();
    testStepScreenshot.setTestStepResultId(testStepResult.getId());
    testStepScreenshot.setEnvironmentResultId(testStepResult.getEnvRunId());
    testStepScreenshot.setTestCaseResultId(testStepResult.getTestCaseResultId());
    testStepScreenshot.setBaseImageName(testStepResult.getScreenshotName());
    testStepScreenshotService.update(testStepScreenshot);
    comparison.setDiffCoordinates("[]");
    comparison = service.update(comparison);
    service.propagateVisualResult(comparison);
    return mapper.map(comparison);
  }
}
