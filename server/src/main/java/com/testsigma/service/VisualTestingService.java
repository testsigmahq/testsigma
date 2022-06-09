/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.config.URLConstants;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.util.HttpClient;
import com.testsigma.web.request.VisualAnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class VisualTestingService {

  public static final String JSON_KEY_PER_SIMILAR = "per_similar";
  public static final String JSON_KEY_DIFF_COORDS = "diff_coordinates";
  public static final String JSON_KEY_ERROR = "error";
  public static final String JSON_KEY_IMAGE_SHAPE = "image_shape";
  public static final String SCREENSHOT_RESULT_ID = "screenshotResultId";

  private final StorageServiceFactory storageServiceFactory;
  private final TestStepScreenshotService testStepScreenshotService;
  private final StepResultScreenshotComparisonService stepResultScreenshotComparisonService;
  private final TestStepResultService testStepResultService;
  private final TestStepService testStepService;
  private final TestCaseResultService testCaseResultService;
  private final HttpClient httpClient;
  private final TestsigmaOSConfigService testsigmaOSConfigService;

  private static String checkNull(String inputString, String defaultString) {
    if (inputString == null || inputString.equals("null")
      || inputString.trim().equals("")) {
      return defaultString;
    }
    return inputString;
  }

  public void initScreenshotComparison(TestCaseResult testCaseResult) throws Exception {
    log.debug("Starting Screenshot comparison for testCaseResult" + testCaseResult);
    List<TestStepResult> stepResultList = testStepResultService.findAllByTestCaseResultIdAndScreenshotNameIsNotNullAndVisualEnabledIsTrue(testCaseResult.getId());
    if (stepResultList.isEmpty()) {
      log.debug("Empty steps for testCaseResult" + testCaseResult);
      return;
    }
    log.debug("No of steps fetched for screenshot Comparison where screenshot name is not null: " + stepResultList.size());
    for (TestStepResult stepResult : stepResultList) {
      initScreenshotComparison(stepResult, testCaseResult);
    }
    List<StepResultScreenshotComparison> failedList = stepResultScreenshotComparisonService.findAllByTestCaseResultIdAndDiffCoordinatesNot(testCaseResult.getId(), "[]");
    log.debug("Count of visually different steps: " + failedList.size());
    testCaseResultService.updateVisualResult(testCaseResult, failedList.isEmpty());
    testCaseResultService.propagateVisualResult(testCaseResult);
  }

  public StepResultScreenshotComparison updateVisualResponse(Map<String, Object> result) throws ResourceNotFoundException {
    Long id = new Long((Integer) result.get(SCREENSHOT_RESULT_ID));
    StepResultScreenshotComparison resultScreenshotComparison = stepResultScreenshotComparisonService.find(id);
    resultScreenshotComparison.setImageShape(result.get(JSON_KEY_IMAGE_SHAPE).toString());
    resultScreenshotComparison.setErrorMessage((String) result.get(JSON_KEY_ERROR));
    resultScreenshotComparison.setSimilarityScore(getDoubleValue(result.get(JSON_KEY_PER_SIMILAR)));
    resultScreenshotComparison.setDiffCoordinates(result.get(JSON_KEY_DIFF_COORDS).toString());
    resultScreenshotComparison = stepResultScreenshotComparisonService.update(resultScreenshotComparison);
    stepResultScreenshotComparisonService.propagateVisualResult(resultScreenshotComparison);
    return resultScreenshotComparison;
  }

  private Double getDoubleValue(Object strObj) {
    if (strObj == null || org.apache.commons.lang3.StringUtils.isEmpty(strObj.toString())) {
      return null;
    }
    DecimalFormat df = new DecimalFormat("#.##");
    Double input = (Double) strObj;
    df.setRoundingMode(RoundingMode.DOWN);
    return Double.parseDouble(df.format(input));
  }

  private void initScreenshotComparison(TestStepResult testStepResult, TestCaseResult testCaseResult) throws Exception {
    TestDeviceSettings envSettings = testCaseResult.getTestDeviceResult().getTestDeviceSettings();
    String entityType = testCaseResult.getTestPlanResult().getTestPlan().getEntityType();
    log.info("Starting Screenshot comparision for testStepResult" + testStepResult + " | with envSettings::" + envSettings.toString());
    Optional<TestStepScreenshot> baseScreenshot = getBaseScreenshot(testCaseResult, testStepResult, envSettings, entityType);
    if ((baseScreenshot.isEmpty() || baseScreenshot.get().getBaseImageName() == null) && testStepResult.getResult().equals(ResultConstant.SUCCESS)) {
      log.info(String.format("Base screenshot not found for step id: %s. Saving Base Screenshot for future runs from testStepResult with id: %s...",
              testStepResult.getStepId(), testStepResult.getId()));
      saveAsBaseScreenshot(testCaseResult, testStepResult, envSettings,entityType);
      return;
    }
    if (baseScreenshot.isPresent()) {
      StepResultScreenshotComparison stepResultScreenshotComparison = addResultScreenshotComparison(testStepResult, baseScreenshot.get());
      postImageAnalysisRequest(testStepResult, stepResultScreenshotComparison, baseScreenshot.get());
    }
    else {
      log.debug("BaseScreenshot missing::" + baseScreenshot);
    }
  }

  private Optional<TestStepScreenshot> getBaseScreenshot(TestCaseResult testCaseResult, TestStepResult testStepResult, TestDeviceSettings settings,String entityType) throws Exception {
    String deviceName = checkNull(settings.getDeviceName(), null);
    String browserVersion = checkNull(settings.getBrowser(), null);
    String resolution = checkNull(settings.getResolution(), null);
//    String platformString = checkNull(settings.getPlatform(), "");
    String currentImagePresignedURL = getCurrentRunScreenshotPath(testStepResult);
    String imageSize = getScreenshotImageSize(currentImagePresignedURL);
    if (imageSize == null) {
      return Optional.ofNullable(null);
    }
    String testDataSetName = getTestDataSetName(testCaseResult, testStepResult);
    Long testDataId = getTestDataId(testCaseResult, testStepResult);
    Optional<TestStepScreenshot> baseScreenshot;
    baseScreenshot = testStepScreenshotService.findBaseScreenshotForWeb(testStepResult.getStepId(),deviceName, browserVersion, resolution, testDataSetName, testDataId, imageSize,entityType);
    return baseScreenshot;
  }

  private String getScreenshotImageSize(String imagePresignedURL) {
    //
    BufferedImage imageObj = null;
    for (int i = 1; i <= 12; i++) {
      try {
        imageObj = ImageIO.read(new URL(imagePresignedURL));
        if (imageObj != null) {
          break;
        }
        Thread.sleep(10000);
      } catch (Exception e) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        log.error("Unable to download base image.", e);
      }

    }
    return (imageObj == null ? null : String.format("%sx%s", imageObj.getWidth(), imageObj.getHeight()));
  }

  private Long getTestDataId(TestCaseResult testCaseResult, TestStepResult testStepResult) throws ResourceNotFoundException {
    Long testDataId = null;
    //First we need to check FOR loop bcz there can be a for loop inside a data driven test. In this case for loop executes with for loops test data.
    if (testStepResult.getParentResultId() != null) {
      TestStepResult parentStepResult = testStepResultService.find(testStepResult.getParentResultId());
      TestStep testStep = testStepService.find(parentStepResult.getStepId());
      return (testStep.getForLoopTestDataId() != null) ? testStep.getForLoopTestDataId() : 0;
    }
    if (testCaseResult.getParentId() != null && testCaseResult.getTestDataId() != null) {
      testDataId = testCaseResult.getTestDataId();
    }
    return testDataId;
  }

  private String getTestDataSetName(TestCaseResult testCaseResult, TestStepResult testStepResult) throws ResourceNotFoundException {
    String testDataSetName = null;
    //First we need to check FOR loop bcz there can be a for loop inside a data driven test. In this case for loop executes with for loops test data.
    if (testStepResult.getParentResultId() != null) {
      TestStepResult parentStepResult = testStepResultService.find(testStepResult.getParentResultId());
      StepResultMetadata metadata = parentStepResult.getMetadata();
      if (metadata != null) {
        StepResultForLoopMetadata forLoopData = parentStepResult.getMetadata().getForLoop();
        StepResultWhileLoopMetadata whileLoopData = parentStepResult.getMetadata().getWhileLoop();
        if (forLoopData != null) {
          return forLoopData.getIteration();
        } else if (whileLoopData != null) {
          return null;
        }
      }
    }
    if (testCaseResult.getParentId() != null && testCaseResult.getTestDataId() != null) {
      testDataSetName = testCaseResult.getTestDataSetName();
    }
    return testDataSetName;
  }

  private void saveAsBaseScreenshot(TestCaseResult testCaseResult, TestStepResult testStepResult, TestDeviceSettings envSettings,String entityType) throws Exception {
    Double browserVer = 0.0;
    try {
      browserVer = Double.parseDouble(checkNull(envSettings.getBrowserVersion(), ""));
    } catch (Exception e) {
      e.printStackTrace();
    }
    String currentImagePresignedURL = getCurrentRunScreenshotPath(testStepResult);
    String baseImageSize = getScreenshotImageSize(currentImagePresignedURL);
    String testDataSetName = getTestDataSetName(testCaseResult, testStepResult);
    Long testDataId = getTestDataId(testCaseResult, testStepResult);
    TestStepScreenshot entity = new TestStepScreenshot();
    entity.setTestCaseResultId(testStepResult.getTestCaseResultId());
    entity.setTestStepId(testStepResult.getStepId());
    entity.setTestStepResultId(testStepResult.getId());
    entity.setEnvironmentResultId(testStepResult.getEnvRunId());
    entity.setBaseImageName(testStepResult.getScreenshotName());
    entity.setBrowser(checkNull(envSettings.getBrowser(), null));
    entity.setBrowserVersion(browserVer);
    entity.setScreenResolution(checkNull(envSettings.getResolution(), null));
    entity.setIgnoredCoordinates(null);
    entity.setDeviceName(checkNull(envSettings.getDeviceName(), null));
    entity.setDeviceOsVersion(checkNull(envSettings.getBrowser(), null));
    entity.setTestDataSetName(testDataSetName);
    entity.setTestDataId(testDataId);
    entity.setBaseImageSize(baseImageSize);
    entity.setEntityType(entityType);
    log.debug("Save as base screenshot:" + entity);
    testStepScreenshotService.create(entity);
  }

  private StepResultScreenshotComparison addResultScreenshotComparison(TestStepResult stepResult, TestStepScreenshot baseScreenshot) {
    StepResultScreenshotComparison result = new StepResultScreenshotComparison();
    result.setTestStepId(stepResult.getStepId());
    result.setTestStepResultId(stepResult.getId());
    result.setTestStepBaseScreenshotId(baseScreenshot.getId());
    result.setTestCaseResultId(stepResult.getTestCaseResultId());
    return this.stepResultScreenshotComparisonService.create(result);
  }

  private void postImageAnalysisRequest(TestStepResult testStepResult, StepResultScreenshotComparison stepResultScreenshotComparison, TestStepScreenshot baseScreenshot) throws TestsigmaException {
    VisualAnalysisRequest requestBean = new VisualAnalysisRequest();
    requestBean.setScreenshotResultId(stepResultScreenshotComparison.getId());
    requestBean.setAction("COMPARE");
    requestBean.setBaseImagePath(getBaseScreenshotURL(baseScreenshot));
    requestBean.setCurrentRunScreenshotPath(getCurrentRunScreenshotPath(testStepResult));
    requestBean.setIgnoreCoordinates("[]");
    if (baseScreenshot.getIgnoredCoordinates() != null)
      requestBean.setIgnoreCoordinates(baseScreenshot.getIgnoredCoordinates());
    com.testsigma.util.HttpResponse<Map<String, Object>> response = httpClient.post(
      testsigmaOSConfigService.getUrl() + URLConstants.VISUAL_API_URL,
      getHeaderList(), requestBean, new TypeReference<>() {
      });
    updateVisualResponse(response.getResponseEntity());
    log.debug(String.format("Posted image analysis request status for TestStepResult ID: %s, Test-Step_id: %s is::%s",
      testStepResult.getId(), testStepResult.getStepId(), response.getStatusCode()));
  }

  private List<Header> getHeaderList() {
    Header authorization = new BasicHeader(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + testsigmaOSConfigService.find().getAccessKey());
    Header contentType = new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json; " + StandardCharsets.UTF_8);
    return Lists.newArrayList(contentType, authorization);
  }

  private String getBaseScreenshotURL(TestStepScreenshot testStepScreenshot) {
    Long componentTestCaseResultId = testStepScreenshot.getTestStepResult().getGroupResultId();
    Long testCaseResultId =  componentTestCaseResultId != null ? componentTestCaseResultId : testStepScreenshot.getTestCaseResultId();
    String baseLineImageName = testStepScreenshot.getBaseImageName();
    String screenShotPath =
      "/executions/" + testCaseResultId.toString() + "/" + baseLineImageName;
    return storageServiceFactory.getStorageService().generatePreSignedURL(screenShotPath, StorageAccessLevel.READ).toString();
  }

  private String getCurrentRunScreenshotPath(TestStepResult testStepResult) {
    Long testCaseResultId = testStepResult.getGroupResultId() != null ? testStepResult.getGroupResultId() : testStepResult.getTestCaseResultId();
    String currentScreenShotPath =
      "/executions/" + testCaseResultId.toString() + "/" + testStepResult.getScreenshotName();
    return storageServiceFactory.getStorageService().generatePreSignedURL(currentScreenShotPath, StorageAccessLevel.READ).toString();
  }


}
