package com.testsigma.controller.recorder;

import com.testsigma.dto.WorkspaceDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.WorkspaceMapper;
import com.testsigma.mapper.recorder.ApplicationMapper;
import com.testsigma.model.Workspace;
import com.testsigma.model.recorder.ApplicationDTO;
import com.testsigma.service.WorkspaceService;
import com.testsigma.specification.ApplicationSpecificationsBuilder;
import com.testsigma.web.request.WorkspaceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(path = "/os_recorder/applications")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationController {

    private final ApplicationMapper applicationMapper;
    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    @GetMapping
    public Page<ApplicationDTO> index(ApplicationSpecificationsBuilder builder,
                                      Pageable pageable) {
        log.info("Request /os_recorder/applications");
        Specification<Workspace> spec = builder.build();
        Page<Workspace> versions = workspaceService.findAll(spec, pageable);
        List<ApplicationDTO> dtos = applicationMapper.mapDTOs(workspaceMapper.map(versions.getContent()));
        return new PageImpl<>(dtos, pageable, versions.getTotalElements());
    }
}
