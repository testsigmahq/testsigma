package com.testsigma.repository;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.AddonPluginTestDataFunctionParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class AddonPluginTestDataFunctionParameterService {
  private final AddonPluginTestDataFunctionParameterRepository parameterRepository;

  public List<AddonPluginTestDataFunctionParameter> findByTestDataFunctionId(Long functionId) throws ResourceNotFoundException {
    return this.parameterRepository.findByTestDataFunctionId(functionId);
  }

}
