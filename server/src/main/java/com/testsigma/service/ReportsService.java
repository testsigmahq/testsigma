package com.testsigma.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.*;
import com.testsigma.dto.export.TestCaseCloudXMLDTO;
import com.testsigma.dto.export.TestCaseXMLDTO;
import com.testsigma.event.EventType;
import com.testsigma.event.TestCaseEvent;
import com.testsigma.exception.*;
import com.testsigma.mapper.TestCaseMapper;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.*;
import com.testsigma.repository.ReportsRepository;
import com.testsigma.repository.TagRepository;
import com.testsigma.repository.TestCaseRepository;
import com.testsigma.specification.*;
import com.testsigma.web.request.TestCaseCopyRequest;
import com.testsigma.web.request.TestCaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
@Log4j2
public class ReportsService {

    private final ReportsRepository reportsRepository;

    private final TestCaseService testCaseService;

    private final TestCaseRepository testCaseRepository;

    private final EntityManager entityManager;
    public JSONArray getReport(Long reportId){
        JSONArray reportObject = new JSONArray();
        Optional<Report> report = reportsRepository.findById(reportId);
        if(report.isPresent()){
            Report obtainedReport = report.get();
            ReportModule reportModule = obtainedReport.getReportModule();
            ReportConfiguration reportConfiguration = obtainedReport.getReportConfiguration();
            reportObject = generateReportsFromModule(obtainedReport,reportModule,reportConfiguration);
        }
        return reportObject;
    }

    public JSONArray generateReportsFromModule(Report report, ReportModule reportModule, ReportConfiguration reportConfiguration){
        try{
            BaseSpecificationsBuilder specificationsBuilder = convertCriteriaToSpecification(reportConfiguration,report.getWorkspaceVersion(),Class.forName(reportModule.getBuilderClass()));
            String responseString = getResponseDataFromService(reportModule,specificationsBuilder);
            return new JSONArray(responseString);
        }catch(Exception e){
            log.error("Error while getting the Class");
            return new JSONArray();
        }
    }

    public BaseSpecificationsBuilder convertCriteriaToSpecification(ReportConfiguration reportConfiguration, WorkspaceVersion version, Class builderClass) throws Exception{
        SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, version.getId());
        List<SearchCriteria> params = new ArrayList<>();
        params.add(criteria);
        List<ReportConfigurationCriteriaMappings> mappings = reportConfiguration.getReportCriteriaMappings();
        for(ReportConfigurationCriteriaMappings criteriaMapping:mappings){
            ReportCriteria reportCriteria = criteriaMapping.getCriteria();
            String key = reportCriteria.getCriteriaField().getFieldName();
            Object value = reportCriteria.getCriteriaValue();
            SearchCriteria reportSearchCriteria = new SearchCriteria(key,reportCriteria.getCriteriaCondition(),value);
            params.add(reportSearchCriteria);
        }
        BaseSpecificationsBuilder specificationsBuilder = (BaseSpecificationsBuilder) builderClass.getDeclaredConstructor().newInstance();
        specificationsBuilder.params = params;
        return specificationsBuilder;
    }

    public String getResponseDataFromService(ReportModule reportModule, BaseSpecificationsBuilder specificationsBuilder){
        if(reportModule.getModuleName().equalsIgnoreCase("TestCase")){
            Specification<TestCase> specification = ((TestCaseSpecificationsBuilder)specificationsBuilder).build();
            return new ObjectMapperService().convertToJson(testCaseService.findAll(specification,Pageable.unpaged()).getContent());
        }
        return "";
    }

    public List<FlakyTestsDTO> getFlakyTests(Long versionId){
        return testCaseRepository.getFlakyTests(versionId);
    }

    public List<RunDurationTrendDTO> getRunDurationTrend(Long versionId){
        return testCaseRepository.getRunDurationTrend(versionId);
    }

    public List<TopFailuresDTO> getTopFailures(Long versionId){
        return testCaseRepository.getTopFailures(versionId);
    }

    public List<LingeredTestsDTO> getLingeredTests(Long versionId){
        Pageable pageable = PageRequest.of(0,10);
        return testCaseRepository.getLingeredTests(versionId,pageable);
    }

    public List<FailuresByCategoryDTO> getFailuresByCategory(Long versionId){
        return testCaseRepository.getFailuresByCategory(versionId);
    }

    public List<Map<String,Object>> getQueryReport(String query){
        try{
        Query nativeQuery = entityManager.createNativeQuery(query);
        List<Object> resultObject = nativeQuery.getResultList();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String,Object>> toBeReturned = new ArrayList<Map<String,Object>>();
        Integer i = 0;
        for(Object result:resultObject){
            String queryResult = objectMapper.writeValueAsString(result);
            queryResult = queryResult.substring(1); // index starts at zero
            queryResult = queryResult.substring(0, queryResult.length() - 1);
            List<Object> resultList = new ArrayList<Object>(Arrays.asList(queryResult.split(",")));
            Map<String, Object> resultIndex = new HashMap<>();
            resultIndex.put(i.toString(),resultList);
            toBeReturned.add(resultIndex);
            i++;
        }
        return toBeReturned;
        } catch(Exception e){
            log.error("This shouldnt come.....");
            return new ArrayList<Map<String,Object>>();
        }
    }
}
