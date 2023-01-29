/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.dto.*;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.mapper.ReportsMapper;
import com.testsigma.model.*;
import com.testsigma.repository.ReportsRepository;
import com.testsigma.service.*;
import com.testsigma.specification.ElementSpecificationsBuilder;
import com.testsigma.specification.ReportsSpecificationBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.ElementScreenNameRequest;
import com.testsigma.web.request.TestCaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping(path = "/reports")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReportsController {
    private final ReportsService reportsService;

    private final ReportsMapper reportsMapper;

    private final ReportsRepository reportsRepository;


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ReportsDTO show(@PathVariable("id") Long id) throws TestsigmaException,Exception {
        Optional<Report> report = reportsRepository.findById(id);
        return reportsMapper.map(report.get());
    }

    @RequestMapping(value = "/generate_report/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generateReport(@PathVariable("id") Long id) throws TestsigmaException,Exception {
        JSONArray responseObject = reportsService.getReport(id);
        List<Map<String,Object>> entities = new ArrayList<Map<String,Object>>();
        for (int i=0;i<responseObject.length();i++) {
            Map<String, Object> result = new ObjectMapper().readValue(responseObject.getJSONObject(i).toString(), new TypeReference<Map<String, Object>>(){});
            entities.add(result);
        }

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @GetMapping(value = {"/flaky_tests"})
    public List<FlakyTestsDTO> getFlakyTests(@RequestParam("versionId") Long versionId) {
        return this.reportsService.getFlakyTests(versionId);
    }

    @GetMapping(value = {"/run_duration_trend"})
    public List<RunDurationTrendDTO> getRunDurationTrend(@RequestParam("versionId") Long versionId) {
        return this.reportsService.getRunDurationTrend(versionId);
    }

    @GetMapping(value = {"/top_failures"})
    public List<TopFailuresDTO> getTopFailures(@RequestParam("versionId") Long versionId) {
        return this.reportsService.getTopFailures(versionId);
    }

    @GetMapping(value = {"/lingered_tests"})
    public List<LingeredTestsDTO> getLingeredTests(@RequestParam("versionId") Long versionId) {
        return this.reportsService.getLingeredTests(versionId);
    }

    @GetMapping(value = {"/failures_by_category"})
    public List<FailuresByCategoryDTO> getFailuresByCategory(@RequestParam("versionId") Long versionId) {
        return this.reportsService.getFailuresByCategory(versionId);
    }

    @RequestMapping(value = "/generate_query_report", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> update(@RequestBody String query) throws TestsigmaException, SQLException, CloneNotSupportedException {
        List<Map<String,Object>> entities = this.reportsService.getQueryReport(query);
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<ReportsDTO> index(ReportsSpecificationBuilder builder,
                                  @PageableDefault(value = 25, page = 0) Pageable pageable) {
        log.debug("GET /reports");
        Specification<Report> spec = builder.build();
        Page<Report> reports = reportsService.findAll(spec, pageable);
        List<ReportsDTO> testCaseDTOS = reportsMapper.mapDTOs(reports.getContent());
        return new PageImpl<>(testCaseDTOS, pageable, reports.getTotalElements());
    }

}
