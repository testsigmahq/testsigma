package com.testsigma.schedulers;

import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.repository.TestDataProfileRepository;
import com.testsigma.service.TestDataSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDataMigrationScheduler {

    private final TestDataProfileRepository testDataProfileRepository;
    private final TestDataSetService testDataSetService;

    @Scheduled(cron = "* 0/30 * * * *")
    public void migrateTestData() {
        List<TestData> testDataList = testDataProfileRepository.findAll();
        for(TestData testData: testDataList){
            if(testData.getIsMigrated()==null || !testData.getIsMigrated()) {
                log.info("Migrating testdata of Id : " + testData.getId());
                List<TestDataSet> testDataSets = testData.getTempTestData();
                Long index = 0L;
                for (TestDataSet testDataSet : testDataSets) {
                    testDataSet.setTestDataId(testData.getId());
                    testDataSet.setPosition(index);
                    testDataSetService.create(testDataSet);
                    index++;
                }
                testData.setIsMigrated(true);
                this.testDataProfileRepository.save(testData);
                log.info("Migration completed testdata of Id : " + testData.getId());
            }
            else
                log.info("TestData already migrated so skipping migration of testdata Id : ", testData.getId());
        }
    }
}

