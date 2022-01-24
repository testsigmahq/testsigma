/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestCaseTypeXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCaseTypeMapper;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.TestCaseType;
import com.testsigma.repository.TestCaseTypeRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestCaseTypeSpecificationsBuilder;
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
public class TestCaseTypeService extends XMLExportService<TestCaseType> {

  private final TestCaseTypeRepository testCaseTypeRepository;
  private final WorkspaceVersionService workspaceVersionService;
  private final TestCaseTypeMapper mapper;

  public Page<TestCaseType> findAll(Specification<TestCaseType> spec, Pageable pageable) {
    return this.testCaseTypeRepository.findAll(spec, pageable);
  }

  public List<TestCaseType> findAll() {
    return this.testCaseTypeRepository.findAll();
  }

  public TestCaseType find(Long id) throws ResourceNotFoundException {
    return this.testCaseTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestCaseType missing"));
  }

  public TestCaseType update(TestCaseType testCaseType) {
    return this.testCaseTypeRepository.save(testCaseType);
  }

  public TestCaseType create(TestCaseType testCaseType) {
    return this.testCaseTypeRepository.save(testCaseType);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestCaseType testCaseType = find(id);
    this.testCaseTypeRepository.delete(testCaseType);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsTestCaseTypeEnabled()) return;
    log.debug("backup process for testcase type initiated");
    writeXML("testcase_types", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for testcase type completed");
  }

  @Override
  protected List<TestCaseTypeXMLDTO> mapToXMLDTOList(List<TestCaseType> list) {
    return mapper.mapTestCaseTypes(list);
  }

  public Specification<TestCaseType> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    WorkspaceVersion applicationVersion = workspaceVersionService.find(backupDTO.getWorkspaceVersionId());
    SearchCriteria criteria = new SearchCriteria("workspaceId", SearchOperation.EQUALITY, applicationVersion.getWorkspace().getId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    TestCaseTypeSpecificationsBuilder applicationSpecificationsBuilder = new TestCaseTypeSpecificationsBuilder();
    applicationSpecificationsBuilder.params = params;
    return applicationSpecificationsBuilder.build();
  }
}
