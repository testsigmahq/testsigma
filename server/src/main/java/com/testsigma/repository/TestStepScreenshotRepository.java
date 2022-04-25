/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.TestStepScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface TestStepScreenshotRepository extends JpaRepository<TestStepScreenshot, Long> {
  Optional<TestStepScreenshot> findByTestStepIdAndDeviceName(Long testStepId, String deviceName);

  Optional<TestStepScreenshot> findByTestStepIdAndBrowserAndScreenResolution(Long testStepId, String browser, String screenResolution);

  @Query("SELECT testStepScreenshot FROM  TestStepScreenshot testStepScreenshot where testStepScreenshot.testStepId = :stepId  " +
          "AND testStepScreenshot.entityType =:entityType "+
    "AND (:deviceName is null OR testStepScreenshot.deviceName = :deviceName) " +
    "AND (:testDataSetName is null OR testStepScreenshot.testDataSetName = :testDataSetName) " +
    "AND (:testDataId is null OR testStepScreenshot.testDataId = :testDataId)" +
    "AND (:baseImageSize is null OR testStepScreenshot.baseImageSize = :baseImageSize) ")
  Optional<TestStepScreenshot> findBaseScreenshotForMobile(@Param("stepId") Long stepId,
                                                           @Param("deviceName") String deviceName,
                                                           @Param("testDataSetName") String testDataSetName,
                                                           @Param("testDataId") Long testDataId,
                                                           @Param("baseImageSize") String baseImageSize,
                                                           @Param("entityType")String entityType);

  @Query("SELECT testStepScreenshot FROM  TestStepScreenshot testStepScreenshot where testStepScreenshot.testStepId = :stepId  " +
          "AND testStepScreenshot.entityType = :entityType " +
    "AND (:deviceName is null OR testStepScreenshot.deviceName = :deviceName) " +
    "AND (:browser is null OR testStepScreenshot.browser = :browser) AND (:screenResolution is null OR testStepScreenshot.screenResolution = :screenResolution) " +
    "AND (:testDataSetName is null OR testStepScreenshot.testDataSetName = :testDataSetName) " +
    "AND (:testDataId is null OR testStepScreenshot.testDataId = :testDataId) " +
    "AND (:baseImageSize is null OR testStepScreenshot.baseImageSize = :baseImageSize)")
  Optional<TestStepScreenshot> findBaseScreenshotForWeb(@Param("stepId") Long stepId,
                                                        @Param("deviceName")String deviceName,
                                                        @Param("browser") String browser,
                                                        @Param("screenResolution") String screenResolution,
                                                        @Param("testDataSetName") String testDataSetName,
                                                        @Param("testDataId") Long testDataId,
                                                        @Param("baseImageSize") String baseImageSize,
                                                        @Param("entityType")String entityType);

}
