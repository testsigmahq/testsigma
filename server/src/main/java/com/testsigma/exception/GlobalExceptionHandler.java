/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.exception;

import com.testsigma.dto.APIErrorDTO;
import com.testsigma.dto.FieldErrorDTO;
import com.testsigma.mapper.FieldErrorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private final FieldErrorMapper errorMapper;

  //This exception is thrown when argument annotated with @Valid failed validation:
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                final HttpHeaders headers, final HttpStatus status,
                                                                final WebRequest request) {
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
    fieldErrorDTO.setField(ex.getName());
    fieldErrorDTO.setMessage(" should be of type " + ex.getRequiredType().getName());
    apiError.setFieldErrors(fieldErrorDTOS);
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  //This exception reports the result of constraint violations:
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex,
                                                          final WebRequest request) {
    logger.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    final List<FieldErrorDTO> errors = new ArrayList<FieldErrorDTO>();
    for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      FieldErrorDTO fieldErrorDTO = new FieldErrorDTO();
      fieldErrorDTO.setField(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath());
      fieldErrorDTO.setMessage(violation.getMessage());
    }
    apiError.setFieldErrors(errors);
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  // 404

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
                                                                 final HttpHeaders headers, final HttpStatus status,
                                                                 final WebRequest request) {
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
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
    logger.error(ex.getMessage(), ex);
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(builder.toString());
    return new ResponseEntity<>(apiError, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Object> handle(final ResourceNotFoundException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    apiError.setCode(ex.getErrorCode());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({JwtTokenMissingException.class})
  public ResponseEntity<Object> handle(final JwtTokenMissingException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({InValidJwtTokenException.class})
  public ResponseEntity<Object> handle(final InValidJwtTokenException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({AgentDeletedException.class})
  public ResponseEntity<Object> handleAgentDeleted(final AgentDeletedException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
  }

  @ExceptionHandler({TestsigmaRunTimeDataNotException.class})
  public ResponseEntity<Object> handleRuntime(final TestsigmaRunTimeDataNotException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({NotLocalAgentRegistrationException.class})
  public ResponseEntity<Object> handleRuntime(final NotLocalAgentRegistrationException ex) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
  }

  @ExceptionHandler({TestsigmaException.class})
  public ResponseEntity<Object> handleTestsigmaException(final TestsigmaException ex, final WebRequest request) {
    logger.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setCode(ex.getErrorCode());
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({IntegrationNotFoundException.class})
  public ResponseEntity<Object> handleTestsigmaException(final IntegrationNotFoundException ex,
                                                         final WebRequest request) {
    logger.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setCode(ex.getErrorCode());
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  // 500
  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
    log.error(ex.getMessage(), ex);
    final APIErrorDTO apiError = new APIErrorDTO();
    apiError.setError(ex.getLocalizedMessage());
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({org.springframework.dao.DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDuplicateName(final DataIntegrityViolationException ex) {
    ex.printStackTrace();
    log.error(ex.getMessage(), ex);
    if (ex.getCause().getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class) &&
      ex.getCause().getCause().getMessage().contains("Duplicate entry")) {
      final APIErrorDTO apiError = new APIErrorDTO();
      apiError.setError("Entity with same name already exists, Please use different name");
      return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    } else if (ex.getCause().getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class) &&
      ex.getCause().getCause().getMessage().contains("component_id_key")) {
      final APIErrorDTO apiError = new APIErrorDTO();
      apiError.setError("Can not delete step Group because it is referenced in another Test Case");
      return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    } else if (ex.getCause().getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class) &&
      ex.getCause().getCause().getMessage().contains("Cannot delete or update a parent row")) {
      final APIErrorDTO apiError = new APIErrorDTO();
      apiError.setError("Entity has some relation please check it out");
      return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    } else {
      final APIErrorDTO apiError = new APIErrorDTO();
      apiError.setError(ex.getMessage());
      return new ResponseEntity<>(apiError,
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
