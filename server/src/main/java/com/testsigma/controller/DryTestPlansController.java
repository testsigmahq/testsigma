package com.testsigma.controller;

import com.testsigma.dto.DryTestPlanDTO;
import com.testsigma.dto.TestPlanResultDTO;
import com.testsigma.mapper.DryTestPlanMapper;
import com.testsigma.mapper.TestPlanMapper;
import com.testsigma.mapper.TestPlanResultMapper;
import com.testsigma.model.DryTestPlan;
import com.testsigma.model.TestDevice;
import com.testsigma.service.AgentExecutionService;
import com.testsigma.service.DryTestPlanService;
import com.testsigma.specification.DryTestPlanSpecificationBuilder;
import com.testsigma.web.request.DryTestPlanRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "/dry_test_plans", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class DryTestPlansController {

  private final DryTestPlanService service;
  private final DryTestPlanMapper mapper;
  private final TestPlanMapper testPlanMapper;
  private final TestPlanResultMapper testPlanResultMapper;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;


  @PostMapping
  public TestPlanResultDTO create(@RequestBody @Valid DryTestPlanRequest request) throws Exception {
    log.info("Create Request /dry_test_plans/ with data::" + request);
    DryTestPlan dryTestPlan = this.mapper.map(request);
    dryTestPlan.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    dryTestPlan.setName("Dry run " + new Timestamp(java.lang.System.currentTimeMillis()));
    TestDevice testDevice = this.testPlanMapper.map(request.getTestDevices().get(0));
    testDevice.setTitle(new Timestamp(Calendar.getInstance().getTimeInMillis()).toString());
    testDevice.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testDevice.setTestPlanId(dryTestPlan.getId());
    dryTestPlan = this.service.create(dryTestPlan, testDevice);
    AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
    agentExecutionService.setTestPlan(dryTestPlan);
    agentExecutionService.start();
    return testPlanResultMapper.mapTo(agentExecutionService.getTestPlanResult());
  }

  @GetMapping
  public Page<DryTestPlanDTO> index(DryTestPlanSpecificationBuilder builder, Pageable pageable) {
    Specification<DryTestPlan> spec = builder.build();
    Page<DryTestPlan> dryTestPlans = this.service.findAll(spec, pageable);
    List<DryTestPlanDTO> testPlanDTOS =
      mapper.mapList(dryTestPlans.getContent());
    return new PageImpl<>(testPlanDTOS, pageable, dryTestPlans.getTotalElements());
  }


}
