package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ForLoopConditionXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RestStepMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestStep;
import com.testsigma.model.TestStepType;
import com.testsigma.repository.TestStepRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestStepSpecificationsBuilder;
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.model.ForLoopCondition;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestData;
import com.testsigma.model.TestStep;
import com.testsigma.repository.ForLoopConditionRepository;
import com.testsigma.specification.ForLoopConditionsSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class ForLoopConditionService extends XMLExportImportService<TestStep> {
    private final TestStepRepository repository;
    private final RestStepMapper mapper;
    private final TestCaseService testCaseService;
    private final TestStepMapper exportTestStepMapper;


    public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
        if (!backupDTO.getIsTestStepEnabled()) return;
        log.debug("backup process for for loop test step initiated");
        writeXML("for_loop_conditions", backupDTO, PageRequest.of(0, 25));
        log.debug("backup process for for loop test step completed");
    }

    public Specification<TestStep> getExportXmlSpecification(BackupDTO backupDTO) {
        List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
        List<Long> testcaseIds = testCaseList.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
        SearchCriteria criteria1 = new SearchCriteria("testCaseId", SearchOperation.IN, testcaseIds);
        SearchCriteria criteria2 = new SearchCriteria("type", SearchOperation.EQUALITY, TestStepType.FOR_LOOP);
        List<SearchCriteria> params = new ArrayList<>();
        params.add(criteria1);
        params.add(criteria2);
        TestStepSpecificationsBuilder testStepSpecificationsBuilder = new TestStepSpecificationsBuilder();
        testStepSpecificationsBuilder.params = params;
        return testStepSpecificationsBuilder.build();
    }

    @Override
    protected List<ForLoopConditionXMLDTO> mapToXMLDTOList(List<TestStep> list) {
        return exportTestStepMapper.mapToCloudForConditions(list);
    }

    @Override
    List<TestStep> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException, ResourceNotFoundException {
        return null;
    }

    @Override
    Optional<TestStep> findImportedEntity(TestStep testStep, BackupDTO importDTO) {
        return Optional.empty();
    }

    @Override
    Optional<TestStep> findImportedEntityHavingSameName(Optional<TestStep> previous, TestStep testStep, BackupDTO importDTO) throws ResourceNotFoundException {
        return Optional.empty();
    }

    @Override
    boolean hasImportedId(Optional<TestStep> previous) {
        return false;
    }

    @Override
    boolean isEntityAlreadyImported(Optional<TestStep> previous, TestStep testStep) {
        return false;
    }

    @Override
    TestStep processBeforeSave(Optional<TestStep> previous, TestStep present, TestStep importEntity, BackupDTO importDTO) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public TestStep copyTo(TestStep testStep) {
        return mapper.copy(testStep);
    }

    @Override
    public TestStep save(TestStep testStep) {
        return repository.save(testStep);
    }

    @Override
    public Optional<TestStep> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
        Long importedId = ids[0];
        List<Long> testcaseIds = new ArrayList<>();
        testCaseService.findAllByWorkspaceVersionId(importDTO.getWorkspaceVersionId()).stream()
                .forEach(testCase -> testcaseIds.add(testCase.getId()));
        Optional<TestStep> previous = repository.findByTestCaseIdInAndImportedId(testcaseIds, importedId);
        return previous;
    }

    @Override
    boolean hasToSkip(TestStep testStep, BackupDTO importDTO) {
        return false;
    }

    @Override
    void updateImportedId(TestStep testStep, TestStep previous, BackupDTO importDTO) {

    }

    @Override
    public Page<TestStep> findAll(Specification<TestStep> spec, Pageable pageable) {
        return this.repository.findAll(spec, pageable);
    }

}
