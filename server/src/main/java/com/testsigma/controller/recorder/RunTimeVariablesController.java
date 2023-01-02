package com.testsigma.controller.recorder;

import com.testsigma.model.recorder.RunTimeVariableDTO;
import com.testsigma.service.RunTimeDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/os_recorder/run_time_data", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RunTimeVariablesController {
    private final RunTimeDataService runTimeDataService;

    @RequestMapping(value="/app_version/{id}", method = RequestMethod.GET)
    public List<RunTimeVariableDTO> getCreatedVariables(@PathVariable("id") Long workspaceVersionId) throws Exception {
        return runTimeDataService.getAllRuntimeVariablesInVersion(workspaceVersionId);
    }
}