package com.testsigma.logging;

import com.testsigma.service.ObjectMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoggingService {

  private final ObjectMapperService objectMapperService;
  @Value("${server.requests.body.log}")
  private boolean requestBodyLogEnabled;

  public void logRequest(Object body) {
    log.info("Request Body: " + objectMapperService.convertToJson(body));
  }

  public void logResponse(Object body) {
    if (requestBodyLogEnabled) {
      log.info("Response Body: " + objectMapperService.convertToJson(body));
    }
  }
}
