/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.config.ApplicationConfig;
import com.testsigma.dto.JUNITPropertyDTO;
import com.testsigma.dto.JUNITTestCaseNodeDTO;
import com.testsigma.dto.JUNITTestSuiteNodeDTO;
import com.testsigma.dto.JUNITTestSuitesNodeDTO;
import com.testsigma.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class JunitReportService {
  private final TestPlanService testPlanService;
  private final TestCaseResultService testCaseResultService;
  private final TestDeviceResultService testDeviceResultService;
  private final TestSuiteService testSuiteService;
  private final ApplicationConfig applicationConfig;

  public JUNITTestSuitesNodeDTO generateJunitReport(Long executionId, Long executionResultId) throws Exception {
    JUNITTestSuitesNodeDTO JUNITTestSuitesNodeDTO = new JUNITTestSuitesNodeDTO();
    Map<Long, TestSuite> testSuitesMap = new HashMap<>();

    TestPlan testPlan = testPlanService.find(executionId);
    String resultsURL = generateReportsURL(executionResultId);

    List<TestDeviceResult> testDeviceResults = testDeviceResultService.findAllByTestPlanResultId(executionResultId);
    List<JUNITTestSuiteNodeDTO> JUNITTestSuiteNodeDTOS = new ArrayList<>();
    for (TestDeviceResult testDeviceResult : testDeviceResults) {
      JUNITTestSuiteNodeDTO JUNITTestSuiteNodeDTO = generateTestSuiteNode(testPlan, testDeviceResult, resultsURL, testSuitesMap);
      JUNITTestSuiteNodeDTOS.add(JUNITTestSuiteNodeDTO);
    }

    JUNITTestSuitesNodeDTO.setJUNITTestSuiteNodeDTOS(JUNITTestSuiteNodeDTOS);
    return JUNITTestSuitesNodeDTO;
  }

  private JUNITTestSuiteNodeDTO generateTestSuiteNode(TestPlan testPlan, TestDeviceResult testDeviceResult,
                                                      String resultsURL, Map<Long, TestSuite> testSuitesMap) throws Exception {
    JUNITTestSuiteNodeDTO JUNITTestSuiteNodeDTO = new JUNITTestSuiteNodeDTO();
    List<TestCaseResult> testCaseResults = testCaseResultService.findAllByEnvironmentResultId(testDeviceResult.getId());
    JUNITTestSuiteNodeDTO.setName(testPlan.getName() + " || " + testDeviceResult.getTestDeviceSettings().getTitle());
    JUNITTestSuiteNodeDTO.setTimestamp(testDeviceResult.getStartTime() + "");
    JUNITTestSuiteNodeDTO.setTime(DurationFormatUtils.formatDuration(testDeviceResult.getDuration(),
      "ss.SSS"));
    JUNITTestSuiteNodeDTO.setTests(testCaseResults.size());
    JUNITPropertyDTO property = new JUNITPropertyDTO();
    property.setName("Testsigma reports URL");
    property.setValue(resultsURL);
    List<JUNITPropertyDTO> properties = new ArrayList<>();
    properties.add(property);
    JUNITTestSuiteNodeDTO.setProperties(properties);
    JUNITTestSuiteNodeDTO.setSystemOut("For More info on results, visit " + resultsURL);
    Integer failedOrAborted = 0;
    List<JUNITTestCaseNodeDTO> JUNITTestCaseNodeDTOS = new ArrayList<>();

    for (TestCaseResult testCaseResult : testCaseResults) {
      JUNITTestCaseNodeDTO JUNITTestCaseNodeDTO = generateTestCaseNode(testCaseResult, testSuitesMap, resultsURL);
      if (JUNITTestCaseNodeDTO.hasFailure()) {
        failedOrAborted = failedOrAborted + 1;
      }
      JUNITTestCaseNodeDTOS.add(JUNITTestCaseNodeDTO);
    }
    JUNITTestSuiteNodeDTO.setJUNITTestCaseNodeDTOS(JUNITTestCaseNodeDTOS);
    JUNITTestSuiteNodeDTO.setFailures(failedOrAborted);
    JUNITTestSuiteNodeDTO.setErrors(failedOrAborted);
    return JUNITTestSuiteNodeDTO;
  }

  private JUNITTestCaseNodeDTO generateTestCaseNode(TestCaseResult testCaseResult, Map<Long, TestSuite> testSuitesMap,
                                                    String resultsURL) throws Exception {
    JUNITTestCaseNodeDTO JUNITTestCaseNodeDTO = new JUNITTestCaseNodeDTO();
    JUNITTestCaseNodeDTO.setName(testCaseResult.getTestCaseDetails().getName());
    JUNITTestCaseNodeDTO.setClassName(getTestSuiteName(testCaseResult.getSuiteId(), testSuitesMap));
    if (testCaseResult.getDuration() != null) {
      JUNITTestCaseNodeDTO.setTime(DurationFormatUtils.formatDuration(testCaseResult.getDuration(), "ss.SSS"));
    } else {
      JUNITTestCaseNodeDTO.setTime("00.000");
    }
    if (testCaseResult.getResult() != ResultConstant.SUCCESS) {
      JUNITTestCaseNodeDTO.setFailure(String.format("Test failed with message:\"%s\".please visit below URL for more details.\n %s",
        testCaseResult.getMessage(), resultsURL));
    }
    return JUNITTestCaseNodeDTO;
  }

  private String getTestSuiteName(Long testSuiteId, Map<Long, TestSuite> testSuitesMap) throws Exception {
    if (testSuitesMap.containsKey(testSuiteId)) {
      return testSuitesMap.get(testSuiteId).getName();
    } else {
      TestSuite testSuite = testSuiteService.find(testSuiteId);
      testSuitesMap.put(testSuiteId, testSuite);
      return testSuite.getName();
    }
  }

  private String generateReportsURL(Long executionResultId) {
    String resultsUrl = "/ui/td/runs/{executionResultId}";
    String url = applicationConfig.getServerUrl() + resultsUrl;
    UriTemplate template = new UriTemplate(url);
    Map<String, String> uriVariables = new HashMap<String, String>();
    uriVariables.put("executionResultId", executionResultId.toString());
    return template.expand(uriVariables).toString();
  }

  public String getFormattedXML(Object xmlNode) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(xmlNode.getClass());
    Marshaller marshaller = context.createMarshaller();
    StringWriter xmlWriter = new StringWriter();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.marshal(xmlNode, xmlWriter);
    return xmlWriter.toString();
  }
}
