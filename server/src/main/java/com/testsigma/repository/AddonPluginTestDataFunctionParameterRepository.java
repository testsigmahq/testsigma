package com.testsigma.repository;


import com.testsigma.model.AddonPluginTestDataFunctionParameter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AddonPluginTestDataFunctionParameterRepository extends BaseRepository<AddonPluginTestDataFunctionParameter, Long> {

  void deleteAllByTestDataFunctionId(Long testDataFunctionId);

  List<AddonPluginTestDataFunctionParameter> findByTestDataFunctionId(Long functionId);
}
