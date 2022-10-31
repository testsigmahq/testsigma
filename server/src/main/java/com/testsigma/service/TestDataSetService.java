package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.TestDataSet;
import com.testsigma.repository.TestDataSetRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestDataSetService {
    private final TestDataSetRespository testDataSetRespository;

    public TestDataSet find(Long id) throws ResourceNotFoundException {
        return this.testDataSetRespository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestDataSet missing"));
    }

    public TestDataSet create(TestDataSet testDataSet){
        return this.testDataSetRespository.save(testDataSet);
    }


    public TestDataSet update(TestDataSet testDataSet){
        return this.testDataSetRespository.save(testDataSet);
    }

    public void destroy(Long id) throws ResourceNotFoundException {
        TestDataSet testDataSet = this.find(id);
        this.testDataSetRespository.delete(testDataSet);
    }

    public Page<TestDataSet> findAll(Specification<TestDataSet> specification, Pageable pageable) {
        return testDataSetRespository.findAll(specification, pageable);
    }

    public Page<TestDataSet> findAllByTestDataId(Long testDataId, Pageable page) {
        return testDataSetRespository.findAllByTestDataId(testDataId, page);
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

