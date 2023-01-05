package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ForLoopConditionXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
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
@Transactional
public class ForLoopConditionService {

    private final ForLoopConditionRepository forLoopConditionsRepository;
    private final ForLoopConditionsMapper mapper;
    //private final ForLoopOverriddenMappingService forLoopOverriddenMappingService;
    private final TestCaseService testCaseService;
    private final TestStepService testStepService;
    //private final TestDataService testDataService;

    public ForLoopCondition find(Long id) throws ResourceNotFoundException {
        return forLoopConditionsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ForLoopCondition not found"));
    }

    public Page<ForLoopCondition> findAll(Specification<ForLoopCondition> specification, Pageable pageable) throws ResourceNotFoundException {
        return forLoopConditionsRepository.findAll(specification, pageable);
    }

    protected List<ForLoopConditionXMLDTO> mapToXMLDTOList(List<ForLoopCondition> list) {
        return mapper.mapDtos(list);
    }


    public ForLoopCondition save(ForLoopCondition forLoopConditions) {
        forLoopConditions = forLoopConditionsRepository.save(forLoopConditions);
        return forLoopConditions;
    }

    public Optional<ForLoopCondition> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
        return forLoopConditionsRepository.findByImportedId(ids[0]);

    }


    public boolean hasImportedId(Optional<ForLoopCondition> previous) {
        return previous.isPresent() && previous.get().getImportedId() != null;
    }

    public boolean isEntityAlreadyImported(Optional<ForLoopCondition> previous, ForLoopCondition current) {
        return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
    }


    public boolean hasToSkip(ForLoopCondition forLoopConditions, BackupDTO importDTO) {
        Optional<TestCase> testCase = testCaseService.getRecentImportedEntity(importDTO, forLoopConditions.getTestCaseId());
        return testCase.isEmpty();
    }

    protected void updateImportedId(ForLoopCondition forLoopConditions,
                                    ForLoopCondition previous, BackupDTO importDTO) {
        previous.setImportedId(forLoopConditions.getId());
        save(previous);
    }

    public Optional<ForLoopCondition> findAllByTestCaseStepId(Long stepId) {
        return this.forLoopConditionsRepository.findByTestStepId(stepId);
    }

    public void destroy(Long conditionId) {
        this.forLoopConditionsRepository.deleteById(conditionId);
    }


    public Optional<ForLoopCondition> findById(Long stepId) {
        return this.forLoopConditionsRepository.findById(stepId);
    }
}

