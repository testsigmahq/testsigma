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
import com.testsigma.dto.export.TestCaseTypeCloudXMLDTO;
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
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCaseTypeService extends XMLExportImportService<TestCaseType> {

  private final TestCaseTypeRepository testCaseTypeRepository;
  private final WorkspaceVersionService workspaceVersionService;
  private final TestCaseTypeMapper mapper;

  public Page<TestCaseType> findAll(Specification<TestCaseType> spec, Pageable pageable) {
    return this.testCaseTypeRepository.findAll(spec, pageable);
  }

  public List<TestCaseType> findAll() {
    return this.testCaseTypeRepository.findAll();
  }

  public List<TestCaseType> findByWorkspaceId(Long workspaceId) {
    return this.testCaseTypeRepository.findByWorkspaceId(workspaceId);
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
  protected List<TestCaseTypeCloudXMLDTO> mapToXMLDTOList(List<TestCaseType> list) {
    return mapper.mapToCloudTestCaseTypes(list);
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

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsTestCaseTypeEnabled()) return;
    log.debug("import process for testcase type initiated");
    importFiles("testcase_types", importDTO);
    log.debug("import process for testcase type  completed");
  }

  @Override
  public List<TestCaseType> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapTestCaseTypeCloudList(xmlMapper.readValue(xmlData, new TypeReference<List<TestCaseTypeCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapTestCaseTypeList(xmlMapper.readValue(xmlData,  new TypeReference<List<TestCaseTypeXMLDTO>>() {
      }));
    }
  }

  @Override
  public Optional<TestCaseType> findImportedEntity(TestCaseType testCasePriority, BackupDTO importDTO) {
    return testCaseTypeRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), testCasePriority.getId());
  }

  @Override
  public TestCaseType processBeforeSave(Optional<TestCaseType> previous, TestCaseType present, TestCaseType toImport, BackupDTO importDTO) throws ResourceNotFoundException {
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
  public TestCaseType copyTo(TestCaseType testCasePriority) {
    return mapper.copy(testCasePriority);
  }

  @Override
  public TestCaseType save(TestCaseType testCasePriority) {
    return testCaseTypeRepository.save(testCasePriority);
  }

  @Override
  public Optional<TestCaseType> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedFrom = ids[0];
    return testCaseTypeRepository.findAllByWorkspaceIdAndImportedId(importDTO.getWorkspaceId(), importedFrom);
  }


  public Optional<TestCaseType> findImportedEntityHavingSameName(Optional<TestCaseType> previous, TestCaseType current, BackupDTO importDTO) {
    Optional<TestCaseType> oldEntity = testCaseTypeRepository.findAllByWorkspaceIdAndName(importDTO.getWorkspaceId(), current.getName());
    return oldEntity;
  }

  public boolean hasImportedId(Optional<TestCaseType> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<TestCaseType> previous, TestCaseType current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(TestCaseType requirementType, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(TestCaseType testCaseType, TestCaseType previous, BackupDTO importDTO) {
    previous.setImportedId(testCaseType.getId());
    save(previous);
  }
}
