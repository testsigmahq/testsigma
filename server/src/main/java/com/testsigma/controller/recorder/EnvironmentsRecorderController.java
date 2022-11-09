package com.testsigma.controller.recorder;

import com.testsigma.mapper.EnvironmentMapper;
import com.testsigma.mapper.recorder.EnvironmentRecorderMapper;
import com.testsigma.model.Environment;
import com.testsigma.model.recorder.EnvironmentDTO;
import com.testsigma.service.EnvironmentService;
import com.testsigma.specification.EnvironmentSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/os_recorder/environments")
public class EnvironmentsRecorderController {

    private final EnvironmentMapper environmentMapper;
    private final EnvironmentRecorderMapper environmentRecorderMapper;
    private final EnvironmentService environmentService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<EnvironmentDTO> index(EnvironmentSpecificationsBuilder builder, Pageable pageable) {
        log.info("Get Request /os_recorder/environments");
        Specification<Environment> spec = builder.build();
        Page<Environment> environments = environmentService.findAll(spec, pageable);
        List<EnvironmentDTO> environmentDTOS = environmentRecorderMapper.mapDTOs(environmentMapper.map(environments.getContent()));
        return new PageImpl<>(environmentDTOS, pageable, environments.getTotalElements());
    }
}
