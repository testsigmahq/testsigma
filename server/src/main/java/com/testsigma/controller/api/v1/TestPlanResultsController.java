package com.testsigma.controller.api.v1;

import com.testsigma.dto.api.APITestPlanResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestPlanResultMapper;
import com.testsigma.model.ExecutionTriggeredType;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestPlan;
import com.testsigma.model.TestPlanResult;
import com.testsigma.service.AgentExecutionService;
import com.testsigma.service.TestPlanResultService;
import com.testsigma.service.TestPlanService;
import com.testsigma.specification.TestPlanResultSpecificationsBuilder;
import com.testsigma.web.request.TestPlanResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController(value = "apiExecutionResultsController")
@RequestMapping(path = "/api/v1/test_plan_results")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestPlanResultsController {
  private final TestPlanService testPlanService;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final TestPlanResultService testPlanResultService;
  private final TestPlanResultMapper testPlanResultMapper;


  @GetMapping
  public Page<APITestPlanResultDTO> index(TestPlanResultSpecificationsBuilder builder, @PageableDefault(size = 50) Pageable pageable) {
    Specification<TestPlanResult> spec = builder.build();
    Page<TestPlanResult> testPlanResults = testPlanResultService.findAll(spec, pageable);
    List<APITestPlanResultDTO> testPlanResultDTOS =
      testPlanResultMapper.mapApi(testPlanResults.getContent());
    return new PageImpl<>(testPlanResultDTOS, pageable, testPlanResults.getTotalElements());
  }

  @RequestMapping(method = RequestMethod.POST)
  public APITestPlanResultDTO create(@RequestBody TestPlanResultRequest testPlanResultRequest) throws Exception {
    TestPlan testPlan = this.testPlanService.find(testPlanResultRequest.getTestPlanId());
    AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
    agentExecutionService.setTestPlan(testPlan);
    JSONObject runTimeData = new JSONObject();
    runTimeData.put("build_number", testPlanResultRequest.getBuildNo());
    if (testPlanResultRequest.getRuntimeData() != null) {
      JSONObject runtimeDataObject = new JSONObject(testPlanResultRequest.getRuntimeData());
      runTimeData.put("runtime_data", runtimeDataObject);
    }
    agentExecutionService.setRunTimeData(runTimeData);
    agentExecutionService.setTriggeredType(ExecutionTriggeredType.API);
    agentExecutionService.start();
    return testPlanResultMapper.mapToApi(agentExecutionService.getTestPlanResult());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public APITestPlanResultDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    TestPlanResult testPlanResult = testPlanResultService.find(id);
    return testPlanResultMapper.mapToApi(testPlanResult);
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
  public APITestPlanResultDTO update(@RequestBody TestPlanResultRequest testPlanResultRequest,
                                  @PathVariable(value = "id") Long id) throws Exception {
    TestPlanResult testPlanResult = testPlanResultService.find(id);
    if (testPlanResultRequest.getResult() == ResultConstant.STOPPED) {
      TestPlan testPlan = this.testPlanService.find(testPlanResult.getTestPlanId());
      AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
      agentExecutionService.setTestPlan(testPlan);
      agentExecutionService.stop();
    }
    testPlanResultMapper.merge(testPlanResultRequest, testPlanResult);
    testPlanResultService.update(testPlanResult);
    return testPlanResultMapper.mapToApi(testPlanResult);
  }

}
