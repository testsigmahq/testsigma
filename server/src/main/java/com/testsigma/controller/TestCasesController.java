/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseCoverageSummaryDTO;
import com.testsigma.dto.TestCaseDTO;
import com.testsigma.dto.TestCaseStatusBreakUpDTO;
import com.testsigma.dto.TestCaseTypeBreakUpDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestCaseMapper;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.specification.TestCaseSpecificationsBuilder;
import com.testsigma.util.HttpClient;
import com.testsigma.web.request.TestCaseCopyRequest;
import com.testsigma.web.request.TestCaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(value = "/test_cases")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCasesController {

  private final TestCaseService testCaseService;
  private final TestStepService testStepService;
  private final NaturalTextActionsService templateService;
  private final TestCaseMapper testCaseMapper;
  private final TagService tagService;
  private final AttachmentService attachmentService;
  private final TestCaseFilterService testCaseFilterService;
  private final WorkspaceVersionService versionService;
  private final StepGroupFilterService stepGroupFilterService;
  private final HttpClient httpClient;

  @RequestMapping(path = "/filter/{filterId}", method = RequestMethod.GET)
  public Page<TestCaseDTO> filter(@PathVariable("filterId") Long filterId, @RequestParam("versionId") Long versionId, Pageable pageable) throws ResourceNotFoundException {
    log.debug("GET /test_cases/filter/" + filterId);
    Specification<TestCase> spec = specificationBuilder(filterId, versionId);
    Page<TestCase> testCases = testCaseService.findAll(spec, pageable);
    List<TestCaseDTO> testCaseDTOS = testCaseMapper.mapDTOs(testCases.getContent());
    return new PageImpl<>(testCaseDTOS, pageable, testCases.getTotalElements());
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestCaseDTO> index(TestCaseSpecificationsBuilder builder,
                                 @PageableDefault(value = 25, page = 0) Pageable pageable) {
    log.debug("GET /test_cases");
    Specification<TestCase> spec = builder.build();
    Page<TestCase> testCases = testCaseService.findAll(spec, pageable);
    List<TestCaseDTO> testCaseDTOS = testCaseMapper.mapDTOs(testCases.getContent());
    return new PageImpl<>(testCaseDTOS, pageable, testCases.getTotalElements());
  }

  @RequestMapping(method = RequestMethod.POST)
  public TestCaseDTO create(@RequestBody @Valid TestCaseRequest testCaseRequest) throws TestsigmaException, SQLException {
    log.debug("POST /test_cases with request:" + testCaseRequest);
    TestCase testCase = testCaseService.create(testCaseRequest);
    return testCaseMapper.mapDTO(testCase);
  }

  @PostMapping(path = "/copy")
  public TestCaseDTO copy(@RequestBody @Valid TestCaseCopyRequest testCaseRequest) throws TestsigmaException, SQLException {
    log.debug("POST /test_cases/copy with request:" + testCaseRequest);
    TestCase testCase = testCaseService.copy(testCaseRequest);
    return testCaseMapper.mapDTO(testCase);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public TestCaseDTO show(@PathVariable("id") Long id) throws TestsigmaException {
    TestCase testCase = testCaseService.find(id);
    TestCaseDTO testCaseDTO = testCaseMapper.mapTo(testCase);
    testCaseDTO.setTags(tagService.list(TagType.TEST_CASE, id));
    testCaseDTO.setFiles(attachmentService.findAllByEntityIdAndEntity(id,
      TestCase.class.getName(), PageRequest.of(0, 10)));

    return testCaseDTO;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public TestCaseDTO update(@PathVariable("id") Long id,
                            @RequestBody TestCaseRequest testCase) throws TestsigmaException, SQLException, CloneNotSupportedException {
    log.debug("PUT /test_cases/" + id + "  with request:" + testCase);
    TestCase testcase = testCaseService.update(testCase, id);
    return testCaseMapper.mapDTO(testcase);
  }

  @DeleteMapping(value = "/{id}/mark_as_delete")
  public ResponseEntity<String> markAsDelete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.debug("DELETE /test_cases/mark_as_delete  with request:" + id);
    Long testCaseCountByPreRequisite = testCaseService.testCaseCountByPreRequisite(id);
    if(testCaseCountByPreRequisite==0){
      TestCase testCase = testCaseService.find(id);
      testCase.setDeleted(true);
      testCase.setIsActive(null);
      testCaseService.update(testCase);
      return new ResponseEntity<>("", HttpStatus.OK);
    }
    else{
      return new ResponseEntity<>("Can't Delete Test Case, Used as PreRequisite", HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = {"/mark_as_delete"}, method = RequestMethod.DELETE)
  public ResponseEntity<String> bulkMarkAsDelete(@RequestBody(required = false) Map<String, List<Long>> deleteList, @RequestParam(required = false) List<Long> ids) {
    log.debug("DELETE /test_cases/mark_as_delete  with request:" + deleteList);
    List<Long> validIds = new ArrayList<>();
    if (deleteList != null) {
        ids = deleteList.get("ids");
    }
    for(Long id:ids){
      List<Long> preRequisteIds = testCaseService.getTestCaseIdsByPreRequisite(id);
      if(preRequisteIds.size()==0){
        if(!validIds.contains(id)) {
          validIds.add(id);
        }
      }else{
        if(ids.containsAll(preRequisteIds)){
          for(Long pid: preRequisteIds) {
            if (!validIds.contains(pid) && testCaseService.testCaseCountByPreRequisite(pid)==0) {
              validIds.add(pid);
            }
          }
          if(!validIds.contains(id)) {
            validIds.add(id);
          }
        }
      }
    }
    testCaseService.markAsDelete(validIds);
    if(validIds.size()!= ids.size()){
      return new ResponseEntity<>("Select List contains PreRequisite Test cases", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>("", HttpStatus.OK);
  }

  @RequestMapping(value = {"/restore_delete/{id}"}, method = RequestMethod.PUT)
  public void restore(@PathVariable(value = "id") Long testCaseId) {
    testCaseService.restore(testCaseId);
  }

  @RequestMapping(value = {"/{id}/restore"}, method = RequestMethod.PUT)
  public void restoreNewUI(@PathVariable(value = "id") Long testCaseId) {
    testCaseService.restore(testCaseId);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void destroy(@PathVariable("id") Long id) throws TestsigmaException, IOException {
    testCaseService.destroy(id);
  }

  @GetMapping(value = {"/coverage_summary"})
  public TestCaseCoverageSummaryDTO coverageSummary(@RequestParam("versionId") Long versionId) {
    TestCaseCoverageSummaryDTO summary = new TestCaseCoverageSummaryDTO();
    summary.setAutomatedCount(testCaseService.automatedCountByVersion(versionId));
    return summary;
  }

  @GetMapping(value = {"/break_up_by_status"})
  public List<TestCaseStatusBreakUpDTO> breakUpByStatus(@RequestParam("versionId") Long versionId) {
    return this.testCaseService.breakUpByStatus(versionId);
  }

  @GetMapping(value = {"/break_up_by_type"})
  public List<TestCaseTypeBreakUpDTO> breakUpByType(@RequestParam("versionId") Long versionId) {
    return this.testCaseService.breakUpByType(versionId);
  }

  @RequestMapping(value = "/test_data/{id}", method = RequestMethod.GET)
  public @ResponseBody
  Page<TestCaseDTO> findAllByTestData(@PathVariable(value = "id") Long testDataId,
                                      @PageableDefault(value = 10, page = 0) Pageable pageable) {
    Page<TestCase> testCases = testCaseService.findAllByTestDataId(testDataId, pageable);
    List<TestCaseDTO> dtos = testCaseMapper.mapDTOs(testCases.getContent());
    return new PageImpl<>(dtos, pageable, dtos.size());
  }

  @RequestMapping(value = "/pre_requisite/{id}", method = RequestMethod.GET)
  public @ResponseBody
  Page<TestCaseDTO> findAllByPreRequisite(@PathVariable(value = "id") Long prerequisite,
                                          @PageableDefault(value = 10, page = 0) Pageable pageable) {

    Page<TestCase> testCases = testCaseService.findAllByPreRequisite(prerequisite, pageable);
    List<TestCaseDTO> dtos = testCaseMapper.mapDTOs(testCases.getContent());
    return new PageImpl<>(dtos, pageable, dtos.size());
  }

  private Specification<TestCase> specificationBuilder(Long filterId, Long versionId) throws ResourceNotFoundException {
    ListFilter filter;
    try {
      filter = testCaseFilterService.find(filterId);
    } catch (ResourceNotFoundException e) {
      filter = stepGroupFilterService.find(filterId);
    }
    WorkspaceVersion version = versionService.find(versionId);
    TestCaseSpecificationsBuilder builder = new TestCaseSpecificationsBuilder();
    return builder.build(filter, version);
  }

  @GetMapping(value = "/validateUrls/{id}")
  public @ResponseBody
  ArrayList<String> findAllEmptyElementsByTestCaseId(@PathVariable(value = "id") Long id,
                                                     @RequestParam(value = "currentUrl", required = false) String currentUrl) throws Exception {
    List<TestStep> testSteps = testStepService.findAllByTestCaseIdAndNaturalTextActionIds(
      id,
      templateService.findByDisplayName("navigateTo")
        .stream().map(NaturalTextActions::getId).map(Long::intValue).collect(Collectors.toList())
    );
    ArrayList<String> invalidUrlList = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    if (!StringUtils.isEmpty(currentUrl)) {
      if (invalidUrl(currentUrl)) invalidUrlList.add(currentUrl);
      return invalidUrlList;
    }
    for (TestStep testStep : testSteps) {
      if (testStep.getTestDataType().equals("raw")) {
        urls.add(testStep.getTestData());
        String url = testStep.getTestData();
        if ((url.indexOf("http://localhost") > -1)
          || (url.indexOf("https://localhost") > -1)
          || invalidUrl(url)) {
          invalidUrlList.add(url);
        }
      }
    }
    return invalidUrlList;
  }

  private boolean invalidUrl(String url) {
    HttpURLConnection huc = null;
    try {
      huc = (HttpURLConnection) new URL(url).openConnection();
      huc.setRequestMethod("HEAD");
      huc.getResponseCode();
      return false;
    } catch (Exception ignore) {
      try {
        if (huc != null) {
          huc.disconnect();
        }
      } catch (Exception ignored) {
      }
      return true;
    }
  }
}
