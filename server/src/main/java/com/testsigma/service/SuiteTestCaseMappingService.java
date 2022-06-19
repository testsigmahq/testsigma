/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.SuiteTestCaseMappingXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestSuiteTestCaseMapper;
import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.SuiteTestCaseMapping;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestSuite;
import com.testsigma.repository.SuiteTestCaseMappingRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.SuiteTestCaseMappingSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired,@Lazy}))
public class SuiteTestCaseMappingService extends XMLExportImportService<SuiteTestCaseMapping> {

  private final SuiteTestCaseMappingRepository suiteTestCaseMappingRepository;
  private final TestSuiteService testSuiteService;
  private final TestSuiteTestCaseMapper mapper;
  private final TestCaseService testCaseService;


  public Optional<SuiteTestCaseMapping> findFirstByTestSuiteAndTestCase(AbstractTestSuite testSuite, TestCase testCase) {
    return suiteTestCaseMappingRepository.findFirstByTestSuiteAndTestCase(testSuite, testCase);
  }

  public List<SuiteTestCaseMapping> findAllBySuiteId(Long id) {
    return this.suiteTestCaseMappingRepository.findAllBySuiteId(id);
  }

  public SuiteTestCaseMapping add(SuiteTestCaseMapping suiteTestCaseMapping) {
    return this.suiteTestCaseMappingRepository.save(suiteTestCaseMapping);
  }

  public SuiteTestCaseMapping update(SuiteTestCaseMapping suiteTestCaseMapping) {
    return this.suiteTestCaseMappingRepository.save(suiteTestCaseMapping);
  }

  public Boolean deleteAll(List<SuiteTestCaseMapping> deletableMaps) {
    this.suiteTestCaseMappingRepository.deleteAll(deletableMaps);
    return true;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsSuitesEnabled()) return;
    log.debug("backup process for Testsuite case initiated");
    writeXML("test_suite_test_case_mapping", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for Testsuite case completed");
  }

  protected Page<SuiteTestCaseMapping> findAll(Specification specification, Pageable pageRequest) throws ResourceNotFoundException {
    return suiteTestCaseMappingRepository.findAll(specification, pageRequest);
  }

  @Override
  protected List<SuiteTestCaseMappingXMLDTO> mapToXMLDTOList(List<SuiteTestCaseMapping> list) {
    return mapper.map(list);
  }

  @Override
  public Specification<SuiteTestCaseMapping> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    List<Long> ids = testSuiteService.findAllByVersionId(backupDTO.getWorkspaceVersionId()).stream().map(testSuite -> testSuite.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("suiteId", SearchOperation.IN, ids);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    SuiteTestCaseMappingSpecificationsBuilder suiteTestCaseMappingSpecificationsBuilder = new SuiteTestCaseMappingSpecificationsBuilder();
    suiteTestCaseMappingSpecificationsBuilder.params = params;
    return suiteTestCaseMappingSpecificationsBuilder.build();
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsSuitesEnabled()) return;
    log.debug("import process for Testsuite testcase mapping initiated");
    importFiles("test_suite_test_case_mapping", importDTO);
    log.debug("import process for Testsuite testcase mapping completed");
  }

  @Override
  public List<SuiteTestCaseMapping> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    return mapper.mapXML(xmlMapper.readValue(xmlData, new TypeReference<List<SuiteTestCaseMappingXMLDTO>>() {
    }));
  }

  @Override
  Optional<SuiteTestCaseMapping> findImportedEntity(SuiteTestCaseMapping suiteTestCaseMapping, BackupDTO importDTO) {
    List<Long> ids = testSuiteService.findAllByVersionId(importDTO.getWorkspaceVersionId()).stream().map(testSuite -> testSuite.getId()).collect(Collectors.toList());
    Optional<SuiteTestCaseMapping> previous = suiteTestCaseMappingRepository.findAllBySuiteIdInAndImportedId(ids, suiteTestCaseMapping.getId());
    return previous;
  }

  @Override
  Optional<SuiteTestCaseMapping> findImportedEntityHavingSameName(Optional<SuiteTestCaseMapping> previous, SuiteTestCaseMapping suiteTestCaseMapping, BackupDTO importDTO) throws ResourceNotFoundException {
    return Optional.empty();
  }

  @Override
  boolean hasImportedId(Optional<SuiteTestCaseMapping> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  @Override
  boolean isEntityAlreadyImported(Optional<SuiteTestCaseMapping> previous, SuiteTestCaseMapping suiteTestCaseMapping) {
    return false;
  }

  @Override
  SuiteTestCaseMapping processBeforeSave(Optional<SuiteTestCaseMapping> previous, SuiteTestCaseMapping present, SuiteTestCaseMapping importEntity, BackupDTO importDTO) throws ResourceNotFoundException {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    Optional<TestSuite> testSuite = testSuiteService.getRecentImportedEntity(importDTO, present.getSuiteId());
    if (testSuite.isPresent())
      present.setSuiteId(testSuite.get().getId());

    Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, present.getTestCaseId());
    if (testCase.isPresent())
      present.setTestCaseId(testCase.get().getId());
    return present;
  }

  @Override
  SuiteTestCaseMapping copyTo(SuiteTestCaseMapping suiteTestCaseMapping) {
    return mapper.copy(suiteTestCaseMapping);
  }

  @Override
  SuiteTestCaseMapping save(SuiteTestCaseMapping suiteTestCaseMapping) {
    return suiteTestCaseMappingRepository.save(suiteTestCaseMapping);
  }

  @Override
  Optional<SuiteTestCaseMapping> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    List<Long> testSuiteids = testSuiteService.findAllByVersionId(importDTO.getWorkspaceVersionId()).stream().map(testSuite -> testSuite.getId()).collect(Collectors.toList());
    Optional<SuiteTestCaseMapping> previous = suiteTestCaseMappingRepository.findAllBySuiteIdInAndImportedId(testSuiteids, importedId);
    return previous;
  }

  @Override
  boolean hasToSkip(SuiteTestCaseMapping suiteTestCaseMapping, BackupDTO importDTO) {
    Optional<TestSuite> testSuite = testSuiteService.getRecentImportedEntity(importDTO, suiteTestCaseMapping.getSuiteId());
    Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, suiteTestCaseMapping.getTestCaseId());
    return testSuite.isEmpty() || testCase.isEmpty();
  }

  @Override
  void updateImportedId(SuiteTestCaseMapping suiteTestCaseMapping, SuiteTestCaseMapping previous, BackupDTO importDTO) {
    previous.setImportedId(suiteTestCaseMapping.getId());
    save(previous);
  }
}
