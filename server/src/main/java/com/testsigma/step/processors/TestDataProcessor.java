package com.testsigma.step.processors;

import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.dto.TestCaseStepEntityDTO;
import com.testsigma.mapper.TestCaseMapper;
import com.testsigma.model.TestStepDataMap;
import com.testsigma.service.TestCaseService;
import com.testsigma.service.TestDataProfileService;
import com.testsigma.service.TestStepService;
import lombok.Data;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;


@Data
public abstract class TestDataProcessor {
  protected TestCaseStepEntityDTO testCaseStepEntityDTO;
  protected TestCaseEntityDTO testCaseEntityDTO;
  protected TestDataPropertiesEntity testDataPropertiesEntity;
  protected TestStepDataMap testStepDataMap;
  protected List<String> passwords;
  private final WebApplicationContext context;
  protected TestDataProfileService testDataService;
  protected TestCaseService testCaseService;
  protected TestCaseMapper testCaseMapper;
  protected TestStepService testStepService;
  protected String parameter;
  protected String value;
  protected Boolean isValueSet = false;
  protected Exception exception;


  public TestDataProcessor(TestCaseStepEntityDTO testCaseStepEntityDTO,
                           TestCaseEntityDTO testCaseEntityDTO,
                           TestDataPropertiesEntity testDataPropertiesEntity,
                           WebApplicationContext context) {
    this.context = context;
    this.testCaseEntityDTO = testCaseEntityDTO;
    this.testCaseStepEntityDTO = testCaseStepEntityDTO;
    this.testDataPropertiesEntity = testDataPropertiesEntity;
    this.testDataService = (TestDataProfileService) context.getBean("testDataProfileService");
    this.testCaseService =  (TestCaseService) context.getBean("testCaseService");
    this.testStepService =  (TestStepService) context.getBean("testStepService");
    this.testCaseMapper =  (TestCaseMapper) context.getBean("testCaseMapperImpl");
  }

  abstract public void processTestData() ;

  protected void setErrorMessage(){
    this.isValueSet = true;
  }

}
