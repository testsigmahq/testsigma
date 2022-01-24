package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.KibbutzPluginTestDataFunctionParameter;
import com.testsigma.repository.KibbutzPluginTestDataFunctionParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class KibbutzPluginTestDataFunctionParameterService {
  private final KibbutzPluginTestDataFunctionParameterRepository parameterRepository;

  public List<KibbutzPluginTestDataFunctionParameter> findByTestDataFunctionId(Long functionId) throws ResourceNotFoundException {
    return this.parameterRepository.findByTestDataFunctionId(functionId);
  }

}
