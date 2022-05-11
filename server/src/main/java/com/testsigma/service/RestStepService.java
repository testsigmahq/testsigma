package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.RestStepCloudXMLDTO;
import com.testsigma.dto.export.RestStepXMLDTO;
import com.testsigma.dto.export.TestStepCloudXMLDTO;
import com.testsigma.dto.export.TestStepXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RestStepMapper;
import com.testsigma.model.RestStep;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestStep;
import com.testsigma.repository.RestStepRepository;
import com.testsigma.specification.RestStepSpecificationsBuilder;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestStepService extends XMLExportImportService<RestStep> {
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

  public Page findAll(Specification<RestStep> specification, Pageable pageable) {
    return restStepRepository.findAll(specification, pageable);
  }

  @Override
  protected List<RestStepXMLDTO> mapToXMLDTOList(List<RestStep> list) {
    return mapper.mapRestSteps(list);
  }

  public Specification<RestStep> getExportXmlSpecification(BackupDTO backupDTO) {
    List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
    List<Long> testcaseIds = testCaseList.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
    List<Long> stepIds = testStepService.findAllByTestCaseIdIn(testcaseIds).stream().map(testStep -> testStep.getId()).collect(Collectors.toList());
    SearchCriteria criteria = new SearchCriteria("stepId", SearchOperation.IN, stepIds);
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    RestStepSpecificationsBuilder testStepSpecificationsBuilder = new RestStepSpecificationsBuilder();
    testStepSpecificationsBuilder.params = params;
    return testStepSpecificationsBuilder.build();
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsRestStepEnabled()) return;
    log.debug("import process for rest step  initiated");
    importFiles("rest_steps", importDTO);
    log.debug("import process for rest step  completed");
  }

  @Override
  public List<RestStep> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return mapper.mapRestStepsCloudList(xmlMapper.readValue(xmlData, new TypeReference<List<RestStepCloudXMLDTO>>() {
      }));
    }
    else{
      return mapper.mapRestStepsList(xmlMapper.readValue(xmlData,  new TypeReference<List<RestStepXMLDTO>>() {
      }));
    }
  }


  public Optional<RestStep> findImportedEntity(RestStep restStep, BackupDTO importDTO) {
    Optional<TestStep> step = testStepService.getRecentImportedEntity(importDTO, restStep.getStepId());
    Optional<RestStep> previous = Optional.empty();
    if (step.isPresent())
      previous = restStepRepository.findAllByStepIdAndImportedId(step.get().getId(), restStep.getId());
    return previous;
  }

  public RestStep processBeforeSave(Optional<RestStep> previous, RestStep present, RestStep toImport, BackupDTO importDTO) {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    Optional<TestStep> testStep = testStepService.getRecentImportedEntity(importDTO, present.getStepId());
    if (testStep.isPresent())
      present.setStepId(testStep.get().getId());
    return present;
  }


  public RestStep copyTo(RestStep restStep) {
    RestStep restStepCopy = mapper.mapStep(restStep);
    restStepCopy.setStepId(restStep.getStepId());
    restStepCopy.setId(restStep.getId());
    return restStepCopy;
  }

  public RestStep save(RestStep restStep) {
    return restStepRepository.save(restStep);
  }

  @Override
  public Optional<RestStep> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedFrom = ids[0];
    Long stepId = ids[1];
    Optional<RestStep> previous = restStepRepository.findAllByStepIdAndImportedId(stepId, importedFrom);
    return previous;
  }

  public boolean hasToSkip(RestStep testStep, BackupDTO importDTO) {
    Optional<TestStep> step = testStepService.getRecentImportedEntity(importDTO, testStep.getStepId());
    return step.isEmpty();
  }

  @Override
  void updateImportedId(RestStep restStep, RestStep previous, BackupDTO importDTO) {
    previous.setImportedId(restStep.getId());
    save(previous);
  }

  public Optional<RestStep> findImportedEntityHavingSameName(Optional<RestStep> previous, RestStep current, BackupDTO importDTO) {
    return previous;
  }

  @Override
  public boolean hasImportedId(Optional<RestStep> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<RestStep> previous, RestStep current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

}
