package com.testsigma.schedulers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.mapper.TestStepResultMapper;
import com.testsigma.model.*;
import com.testsigma.service.ForLoopConditionService;
import com.testsigma.service.TestStepResultService;
import com.testsigma.service.TestStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OldResultMigrationScheduler {
    private final TestStepResultService testStepResultService;
    private final TestStepService testStepService;
    private final ForLoopConditionService forLoopConditionService;
    private final ForLoopConditionsMapper forLoopConditionsMapper;
    private final TestStepResultMapper testStepResultMapper;
    private int pageSize = 100;

    @Scheduled(cron = "0 0/5 * * * *")
    public void perform() throws Exception {
        log.debug("old step migration is started");
        int currentPage = 0;
        updateMigrateStepResult(currentPage);
        log.debug("old step migration is completed");
    }

    private void updateMigrateStepResult(Integer currentPage) throws ResourceNotFoundException {
        Timestamp startTime = new Timestamp(2022, 10, 8, 0, 0, 0, 0);
        Page<TestStepResult> testStepResults = testStepResultService.findAllByForLoopTestCaseResultId(startTime, PageRequest.of(currentPage, pageSize));
        for (TestStepResult testStepResult : testStepResults) {
            try {
                TestStep step = testStepService.find(testStepResult.getStepId());
                ForLoopConditionDTO forLoopConditionDTO = null;
                Optional<ForLoopCondition> forLoopConditionOptional = forLoopConditionService.findAllByTestCaseStepId(step.getId());
                if (forLoopConditionOptional.isPresent()) {
                    forLoopConditionDTO = forLoopConditionsMapper.map(forLoopConditionOptional.get());
                }
                else {
                    throw new ResourceNotFoundException("For loop condition not found for test step result id: " + testStepResult.getId());
                }

                StepDetails stepDetails = testStepResult.getStepDetails();
                stepDetails.setNaturalTextActionId(step.getNaturalTextActionId());
                stepDetails.setType(TestStepType.ACTION_TEXT);
                stepDetails.setAction("Loop over data set in ${test-data-profile} from index ${left-data} to index ${right-data}");
                testStepResult.setStepDetails(stepDetails);
                StepResultMetadata stepResultMetadata = testStepResult.getMetadata();
                stepResultMetadata = stepResultMetadata == null ? new StepResultMetadata() : stepResultMetadata;
                StepResultForLoopMetadata stepResultForLoopMetadata = stepResultMetadata.getForLoop();
                stepResultForLoopMetadata = stepResultForLoopMetadata == null ? new StepResultForLoopMetadata() : stepResultForLoopMetadata;
                stepResultForLoopMetadata.setForLoopCondition(forLoopConditionDTO);
                if (stepResultForLoopMetadata.getIndex() != null && stepResultForLoopMetadata.getIndex() > 0)
                    stepResultForLoopMetadata.setIndex(stepResultForLoopMetadata.getIndex() - 1);
                stepResultMetadata.setForLoop(stepResultForLoopMetadata);
                testStepResult.setMetadata(stepResultMetadata);
                testStepResultService.update(testStepResult);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (testStepResults.hasContent()) {
            updateMigrateStepResult(currentPage + 1);
        }
    }
}
