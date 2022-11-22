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

    @Scheduled(cron = "0 0/3 * * * *")
    public void migrateTestData() {
        List<TestData> testDataList = testDataProfileRepository.findAll();
        log.info("Checking if testdata migration is required...");
        for(TestData testData: testDataList){
            if(testData.getIsMigrated()==null || !testData.getIsMigrated()) {
                List<TestDataSet> testDataSets = testData.getTempTestData();
                Long index = 0L;
                for (TestDataSet testDataSet : testDataSets) {
                    testDataSet.setTestDataProfileId(testData.getId());
                    testDataSet.setPosition(index);
                    testDataSetService.create(testDataSet);
                    index++;
                }
                testData.setIsMigrated(true);
                this.testDataProfileRepository.save(testData);
                log.info("Testdata migrated for id: " + testData.getId());
            }
        }
    }
}