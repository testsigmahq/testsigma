/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ForLoopConditionXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.model.*;
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
@Transactional
public class ForLoopConditionService extends XMLExportImportService<ForLoopCondition> {

    private final ForLoopConditionRepository forLoopConditionsRepository;
    private final ForLoopConditionsMapper mapper;
    private final TestCaseService testCaseService;
    private final TestStepService testStepService;

    public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
        log.debug("backup process for for_loop_conditions initiated");
        writeXML("for_loop_conditions", backupDTO, PageRequest.of(0, 25));
        log.debug("backup process for for_loop_conditions completed");
    }

    @Override
    public Page<ForLoopCondition> findAll(Specification<ForLoopCondition> specification, Pageable pageable) throws ResourceNotFoundException {
        return forLoopConditionsRepository.findAll(specification, pageable);
    }

    @Override
    protected List<ForLoopConditionXMLDTO> mapToXMLDTOList(List<ForLoopCondition> list) {
        return mapper.mapDtos(list);
    }


    public Specification<ForLoopCondition> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
        List<TestCase> testCaseList = testCaseService.findAllByWorkspaceVersionId(backupDTO.getWorkspaceVersionId());
        SearchCriteria criteria = new SearchCriteria("testCaseId", SearchOperation.IN, testCaseList.stream().map(testCase ->
                testCase.getId()).collect(Collectors.toList()));
        List<SearchCriteria> params = new ArrayList<>();
        params.add(criteria);
        ForLoopConditionsSpecificationsBuilder forLoopOverridingSpecificationsBuilder = new ForLoopConditionsSpecificationsBuilder();
        forLoopOverridingSpecificationsBuilder.params = params;
        return forLoopOverridingSpecificationsBuilder.build();
    }

    public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
        if (!importDTO.getIsTestStepEnabled()) return;
        log.debug("import process for for_loop_conditions initiated");
        importFiles("for_loop_conditions", importDTO);
        log.debug("import process for for_loop_conditions completed");
    }

    @Override
    public List<ForLoopCondition> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
        return mapper.mapEntities(xmlMapper.readValue(xmlData, new TypeReference<List<ForLoopConditionXMLDTO>>() {
        }));
    }

    @Override
    public Optional<ForLoopCondition> findImportedEntity(ForLoopCondition entity, BackupDTO importDTO) {
        Optional<TestStep> step = testStepService.getRecentImportedEntity(importDTO, entity.getTestStepId());
        Optional<ForLoopCondition> previous = Optional.empty();
        if (step.isPresent()) {
            previous = forLoopConditionsRepository.findByTestStepIdAndType(step.get().getId(), entity.getType());
        }
        return previous;
    }

    @Override
    protected Optional<ForLoopCondition> findImportedEntityHavingSameName(Optional<ForLoopCondition> previous, ForLoopCondition uiIdentifierOverRiddenMapping, BackupDTO importDTO) throws ResourceNotFoundException {
        return Optional.empty();
    }

    @Override
    public ForLoopCondition processBeforeSave(Optional<ForLoopCondition> previous,
                                              ForLoopCondition present, ForLoopCondition toImport,
                                              BackupDTO importDTO) {
        present.setImportedId(present.getId());

        if (previous.isPresent() && importDTO.isHasToReset()) {
            present.setId(previous.get().getId());
        } else {
            present.setId(null);
        }

        if (present.getTestCaseId() != null) {
            Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, present.getTestCaseId());
            if (testCase.isPresent())
                present.setTestCaseId(testCase.get().getId());
        }

        if (present.getTestStepId() != null) {
            Optional<TestStep> testStep = testStepService.getRecentImportedEntity(importDTO, present.getTestStepId());
            if (testStep.isPresent())
                present.setTestStepId(testStep.get().getId());
        }
        return present;
    }

    @Override
    void save(Optional<ForLoopCondition> previous, ForLoopCondition entity, BackupDTO importDTO, ForLoopCondition importEntity) throws ResourceNotFoundException {
        if (!hasToSkip(entity, importDTO)) {
            previous.ifPresent(forLoopCondition -> forLoopConditionsRepository.deleteByIdAndType(forLoopCondition.getId(), forLoopCondition.getType()));
        }
        super.save(previous, entity, importDTO, importEntity);
    }

    @Override
    public ForLoopCondition copyTo(ForLoopCondition testStepDataOverRiddenMapping) {
        Long id = testStepDataOverRiddenMapping.getId();
        testStepDataOverRiddenMapping = mapper.copy(testStepDataOverRiddenMapping);
        testStepDataOverRiddenMapping.setId(id);
        return testStepDataOverRiddenMapping;
    }


    public ForLoopCondition save(ForLoopCondition forLoopConditions) {
        forLoopConditions = forLoopConditionsRepository.save(forLoopConditions);
        return forLoopConditions;
    }

    @Override
    public Optional<ForLoopCondition> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
        return forLoopConditionsRepository.findByImportedId(ids[0]);

    }


    public boolean hasImportedId(Optional<ForLoopCondition> previous) {
        return previous.isPresent() && previous.get().getImportedId() != null;
    }

    public boolean isEntityAlreadyImported(Optional<ForLoopCondition> previous, ForLoopCondition current) {
        return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
    }


    @Override
    public boolean hasToSkip(ForLoopCondition forLoopConditions, BackupDTO importDTO) {
        Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, forLoopConditions.getTestCaseId());
        return testCase.isEmpty();
    }

    @Override
    protected void updateImportedId(ForLoopCondition forLoopConditions,
                                    ForLoopCondition previous, BackupDTO importDTO) {
        previous.setImportedId(forLoopConditions.getId());
        save(previous);
    }

    public void destroy(Long conditionId) {
        this.forLoopConditionsRepository.deleteById(conditionId);
    }


    public Optional<ForLoopCondition> findById(Long stepId) {
        return this.forLoopConditionsRepository.findById(stepId);
    }
}
