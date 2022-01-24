package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.AddonNaturalTextActionParameter;
import com.testsigma.repository.AddonNaturalTextActionParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class AddonNaturalTextActionParameterService {
  private final AddonNaturalTextActionParameterRepository parameterRepository;

  public List<AddonNaturalTextActionParameter> findByAddonId(Long addonId) throws ResourceNotFoundException {
    return this.parameterRepository.findAllByPluginActionId(addonId);
  }

}
