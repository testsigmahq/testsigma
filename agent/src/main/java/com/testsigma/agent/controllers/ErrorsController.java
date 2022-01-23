/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller("/error")
public class ErrorsController implements ErrorController {

  static final String ERROR_PATH = "/error";

  @RequestMapping("/")
  public String handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (status != null) {
      int statusCode = Integer.parseInt(status.toString());

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        return "404";
      } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
        return "500";
      }
    }
    return "error";
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }
}
