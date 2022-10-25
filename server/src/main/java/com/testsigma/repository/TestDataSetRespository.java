package com.testsigma.repository;

import com.testsigma.model.TestDataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestDataSetRespository extends JpaRepository<TestDataSet, Long>, JpaSpecificationExecutor<TestDataSet> {

    List<TestDataSet> findAllByTestDataIdOrderByPosition(Long testDataId);

    @Query("SELECT tds from TestDataSet tds where tds.name in :setNames AND tds.testDataId=:testDataId")
    List<TestDataSet> findAllByNamesAndTestDataId(List<String> setNames, Long testDataId);

    Optional<TestDataSet> findTestDataSetByTestDataIdAndAndName(Long profileId, String name);
}

