/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.TestCasePriorityCloudXMLDTO;
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
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCasePriorityService extends XMLExportImportService<TestCasePriority> {

  private final TestCasePriorityRepository testCasePriorityRepository;
  private final WorkspaceVersionService workspaceVersionService;
  private final TestCasePriorityMapper mapper;

  public Page<TestCasePriority> findAll(Specification<TestCasePriority> spec, Pageable pageable) {
    return this.testCasePriorityRepository.findAll(spec, pageable);
  }

  public List<TestCasePriority> findByWorkspaceId(Long workspaceId) {
    return this.testCasePriorityRepository.findByWorkspaceId(workspaceId);
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

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsTestCasePriorityEnabled()) return;
    log.debug("import process for testcase type initiated");
    importFiles("testcase_priorities", importDTO);
    log.debug("import process for testcase type  completed");
  }

  @Override
  protected List<TestCasePriorityCloudXMLDTO> mapToXMLDTOList(List<TestCasePriority> list) {
    return mapper.mapToCloudTestCasePriorities(list);
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
  @Override
  public List<TestCasePriority> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapCloudTestcasePriorityList(xmlMapper.readValue(xmlData, new TypeReference<List<TestCasePriorityCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapTestcasePriorityList(xmlMapper.readValue(xmlData, new TypeReference<List<TestCasePriorityXMLDTO>>() {
    }));
  }
  }

  @Override
  public Optional<TestCasePriority> findImportedEntity(TestCasePriority testCasePriority, BackupDTO importDTO) {
    return testCasePriorityRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), testCasePriority.getId());
  }

  @Override
  public TestCasePriority processBeforeSave(Optional<TestCasePriority> previous, TestCasePriority present, TestCasePriority toImport, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    present.setWorkspaceId(importDTO.getWorkspaceId());
    return present;
  }


  @Override
  public TestCasePriority copyTo(TestCasePriority testCasePriority) {
    return mapper.copy(testCasePriority);
  }

  @Override
  public TestCasePriority save(TestCasePriority testCasePriority) {
    return testCasePriorityRepository.save(testCasePriority);
  }

  @Override
  public Optional<TestCasePriority> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return testCasePriorityRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), importedId);
  }

  public Optional<TestCasePriority> findImportedEntityHavingSameName(Optional<TestCasePriority> previous, TestCasePriority current, BackupDTO importDTO) {
    Optional<TestCasePriority> oldEntity = testCasePriorityRepository.findAllByWorkspaceIdAndName(importDTO.getWorkspaceId(), current.getName());
    return oldEntity;
  }

  public boolean hasImportedId(Optional<TestCasePriority> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestCasePriority> previous, TestCasePriority current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(TestCasePriority testCasePriority, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(TestCasePriority testCasePriority, TestCasePriority previous, BackupDTO importDTO) {
    previous.setImportedId(testCasePriority.getId());
    save(previous);
  }
}
