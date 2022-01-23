package com.testsigma.controller.api.agent;

import com.testsigma.dto.SuggestionDTO;
import com.testsigma.service.SuggestionMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "agentSuggestionsController")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/api/agents/suggestions")
public class SuggestionsController {
  private final SuggestionMappingService suggestionMappingService;

  @RequestMapping(value = "/{naturalTextActionId}", method = RequestMethod.GET)
  public List<SuggestionDTO> getSuggestions(@PathVariable("naturalTextActionId") Integer naturalTextActionId) {
    return suggestionMappingService.findAllByNaturalTextActionId(naturalTextActionId);
  }
}
