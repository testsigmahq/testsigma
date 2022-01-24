/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestCasePriorityXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCasePriorityMapper;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.TestCasePriority;
import com.testsigma.repository.TestCasePriorityRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestCasePrioritySpecificationsBuilder;
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

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCasePriorityService extends XMLExportService<TestCasePriority> {

  private final TestCasePriorityRepository testCasePriorityRepository;
  private final WorkspaceVersionService workspaceVersionService;
  private final TestCasePriorityMapper mapper;

  public Page<TestCasePriority> findAll(Specification<TestCasePriority> spec, Pageable pageable) {
    return this.testCasePriorityRepository.findAll(spec, pageable);
  }

  public List<TestCasePriority> findAll() {
    return this.testCasePriorityRepository.findAll();
  }

  public TestCasePriority find(Long id) throws ResourceNotFoundException {
    return this.testCasePriorityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestCasePriority missing"));
  }

  public TestCasePriority update(TestCasePriority testCasePriority) {
    return this.testCasePriorityRepository.save(testCasePriority);
  }

  public TestCasePriority create(TestCasePriority testCasePriority) {
    return this.testCasePriorityRepository.save(testCasePriority);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestCasePriority testCasePriority = find(id);
    this.testCasePriorityRepository.delete(testCasePriority);
  }


  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestCasePriorityEnabled()) return;
    log.debug("backup process for testcase priority initiated");
    writeXML("testcase_priorities", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for testcase priority completed");
  }

  @Override
  protected List<TestCasePriorityXMLDTO> mapToXMLDTOList(List<TestCasePriority> list) {
    return mapper.mapTestCasePriorities(list);
  }

  public Specification<TestCasePriority> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    WorkspaceVersion applicationVersion = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("workspaceId", SearchOperation.EQUALITY, applicationVersion.getWorkspace().getId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestCasePrioritySpecificationsBuilder testCasePrioritySpecificationsBuilder = new TestCasePrioritySpecificationsBuilder();
    testCasePrioritySpecificationsBuilder.params = params;
    return testCasePrioritySpecificationsBuilder.build();
  }
}
