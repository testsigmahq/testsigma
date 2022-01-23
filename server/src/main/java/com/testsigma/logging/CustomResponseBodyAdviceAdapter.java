package com.testsigma.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class CustomResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

  @Autowired
  LoggingService loggingService;

  @Override
  public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                ServerHttpResponse serverHttpResponse) {
    try {
      loggingService.logResponse(body);
    } catch (Exception ignored) {
    }
    return body;
  }
}
