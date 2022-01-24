package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.RestStepXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RestStepMapper;
import com.testsigma.model.RestStep;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestStep;
import com.testsigma.repository.RestStepRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestStepSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestStepService extends XMLExportService<TestStep> {
  private final RestStepRepository restStepRepository;
  private final TestStepService testStepService;
  private final TestCaseService testCaseService;
  private final RestStepMapper mapper;

  public RestStep create(RestStep restStep) {
    return this.restStepRepository.save(restStep);
  }

  public RestStep update(RestStep restStep) {
    return this.restStepRepository.save(restStep);
  }

  public RestStep findByStepId(Long stepId) {
    return restStepRepository.findByStepId(stepId);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsRestStepEnabled()) return;
    log.debug("backup process for rest step initiated");
    writeXML("rest_steps", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for rest step completed");
  }

  @Override
  public Page findAll(Specification<TestStep> specification, Pageable pageable) {
    return testStepService.findAll(specification, pageable);
  }

  @Override
  protected List<RestStepXMLDTO> mapToXMLDTOList(List<TestStep> list) {
    return mapper.mapRestSteps(list);
  }

  public Specification<TestStep> getExportXmlSpecification(BackupDTO backupDTO) {
    List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
    List<Long> testcaseIds = testCaseList.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("testCaseId", SearchOperation.IN, testcaseIds);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestStepSpecificationsBuilder testStepSpecificationsBuilder = new TestStepSpecificationsBuilder();
    testStepSpecificationsBuilder.params = params;
    return testStepSpecificationsBuilder.build();
  }
}
