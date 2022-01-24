package com.testsigma.controller;

import com.testsigma.dto.ChromeExtensionDetailsDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ChromeExtensionDetailsMapper;
import com.testsigma.model.ChromeExtensionDetails;
import com.testsigma.service.ChromeExtensionDetailsService;
import com.testsigma.web.request.ChromeExtensionDetailsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping(path = "/chrome_extension_details")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChromeExtensionDetailsController {
  private final ChromeExtensionDetailsService service;
  private final ChromeExtensionDetailsMapper mapper;

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ChromeExtensionDetailsDTO create(@RequestBody ChromeExtensionDetailsRequest chromeExtensionDetailsRequest) throws ResourceNotFoundException, SQLException {
    ChromeExtensionDetails chromeExtensionDetails;
    try {
      service.findOne(chromeExtensionDetailsRequest.getId());
    } catch (Exception e) {
      chromeExtensionDetails = mapper.map(chromeExtensionDetailsRequest);
      chromeExtensionDetails = service.save(chromeExtensionDetails);
      return mapper.map(chromeExtensionDetails);
    }
    throw new ResourceNotFoundException("Chrome extension Details exists please use update");
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  public ChromeExtensionDetailsDTO update(@RequestBody ChromeExtensionDetailsRequest request, @PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
    ChromeExtensionDetails chromeExtensionDetails = service.findOne(request.getId());
    mapper.merge(request, chromeExtensionDetails);
    chromeExtensionDetails = service.save(chromeExtensionDetails);
    return mapper.map(chromeExtensionDetails);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ChromeExtensionDetailsDTO index() throws ResourceNotFoundException, SQLException {
    Long id = Long.valueOf(1);
    ChromeExtensionDetails details = service.findOne(id);
    return mapper.map(details);
  }
}
