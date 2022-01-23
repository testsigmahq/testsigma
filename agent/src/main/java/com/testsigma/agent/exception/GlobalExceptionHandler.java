/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

import com.testsigma.agent.dto.APIErrorDTO;
import com.testsigma.agent.dto.FieldErrorDTO;
import com.testsigma.agent.mappers.FieldErrorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private final FieldErrorMapper errorMapper;
  // 400


  //This exception is thrown when argument annotated with @Valid failed validation:
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                final HttpHeaders headers, final HttpStatus status,
                                                                final WebRequest request) {
    log.info(ex.getClass().getName());
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    apiError.setFieldErrors(errorMapper.map(ex.getBindingResult().getFieldErrors()));
    apiError.setObjectErrors(errorMapper.mapObjectErrors(ex.getBindingResult().getGlobalErrors()));
    return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
  }

  //This exception is thrown when fatal binding errors occur.
  @Override
  protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers,
                                                       final HttpStatus status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    log.info(ex.getClass().getName());
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    apiError.setFieldErrors(errorMapper.map(ex.getBindingResult().getFieldErrors()));
    apiError.setObjectErrors(errorMapper.mapObjectErrors(ex.getBindingResult().getGlobalErrors()));
    return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
  }

  //This exception is thrown when there is type mis match of parameter
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,
                                                      final HttpStatus status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
    fieldErrorDTO.setField(ex.getPropertyName());
    fieldErrorDTO.setMessage(" value should be of type " + ex.getRequiredType());
    fieldErrorDTO.setRejectedValue(ex.getValue());
    fieldErrorDTOS.add(fieldErrorDTO);
    apiError.setFieldErrors(fieldErrorDTOS);
    return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
  }

  //This exception is thrown when when the part of a multipart request not found
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
                                                                   final HttpHeaders headers, final HttpStatus status,
                                                                   final WebRequest request) {
    logger.info(ex.getClass().getName());
    //
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
    fieldErrorDTO.setField(ex.getRequestPartName());
    fieldErrorDTO.setMessage(" part is missing");
    apiError.setFieldErrors(fieldErrorDTOS);
    return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
  }

  //This exception is thrown when request missing parameter:
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
    final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
    final WebRequest request) {
    logger.info(ex.getClass().getName());
    //
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
    fieldErrorDTO.setField(ex.getParameterName());
    fieldErrorDTO.setMessage(" parameter is missing");
    apiError.setFieldErrors(fieldErrorDTOS);
    return new ResponseEntity<>(apiError, headers, HttpStatus.BAD_REQUEST);
  }

  //This exception is thrown when method argument is not the expected type:
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
                                                                 final WebRequest request) {
    logger.info(ex.getClass().getName());
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
    fieldErrorDTO.setField(ex.getName());
    fieldErrorDTO.setMessage(" should be of type " + ex.getRequiredType().getName());
    apiError.setFieldErrors(fieldErrorDTOS);
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  // 404
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
                                                                 final HttpHeaders headers, final HttpStatus status,
                                                                 final WebRequest request) {
    logger.info(ex.getClass().getName());
    //
    final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(error);
    return new ResponseEntity<>(apiError, headers, HttpStatus.NOT_FOUND);
  }

  // 405
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex,
                                                                       final HttpHeaders headers,
                                                                       final HttpStatus status,
                                                                       final WebRequest request) {
    logger.info(ex.getClass().getName());
    //
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(builder.toString());
    return new ResponseEntity<>(apiError, headers, HttpStatus.METHOD_NOT_ALLOWED);
  }

  // 415
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
                                                                   final HttpHeaders headers, final HttpStatus status,
                                                                   final WebRequest request) {
    logger.info(ex.getClass().getName());
    //
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(builder.toString());
    return new ResponseEntity<>(apiError, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler({TestsigmaException.class})
  public ResponseEntity<Object> handleTestsigmaException(final TestsigmaException ex, final WebRequest request) {
    logger.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 500
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
    logger.info(ex.getClass().getName());
    logger.error("error", ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {
    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
      request.setAttribute("javax.servlet.error.exception", ex, 0);
    }

    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, headers, status);
  }

}
