package com.testsigma.repository;


import com.testsigma.model.KibbutzPluginTestDataFunctionParameter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface KibbutzPluginTestDataFunctionParameterRepository extends BaseRepository<KibbutzPluginTestDataFunctionParameter, Long> {

  void deleteAllByTestDataFunctionId(Long testDataFunctionId);

  List<KibbutzPluginTestDataFunctionParameter> findByTestDataFunctionId(Long functionId);
}
