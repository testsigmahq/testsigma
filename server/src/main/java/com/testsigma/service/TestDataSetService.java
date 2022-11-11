package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.TestDataSet;
import com.testsigma.repository.TestDataSetRepository;
import com.testsigma.web.request.TestDataSetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestDataSetService {
    private final TestDataSetRepository testDataSetRepository;

    public TestDataSet find(Long id) throws ResourceNotFoundException {
        return this.testDataSetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestDataSet missing"));
    }

    public TestDataSet create(TestDataSet testDataSet){
        return this.testDataSetRepository.save(testDataSet);
    }

    public Optional<TestDataSet> findByProfileIdAndSetName(Long profileId, String setName){
        return testDataSetRepository.findTestDataSetByTestDataIdAndAndName(
                profileId, setName);
    }

    public TestDataSet update(TestDataSet testDataSet){
        return this.testDataSetRepository.save(testDataSet);
    }

    public void destroy(Long id) throws ResourceNotFoundException {
        TestDataSet testDataSet = this.find(id);
        this.testDataSetRepository.delete(testDataSet);
    }

    public Page<TestDataSet> findAll(Specification<TestDataSet> specification, Pageable pageable) {
        return testDataSetRepository.findAll(specification, pageable);
    }

    public void bulkDestroy(Long[] ids) throws Exception {
        Boolean allIdsDeleted = true;
        Exception throwable = new Exception();
        for (Long id : ids) {
            try {
                destroy(id);
            } catch (Exception ex) {
                allIdsDeleted = false;
                throwable = ex;
            }
        }
        if (!allIdsDeleted) {
            throw throwable;
        }
    }
}