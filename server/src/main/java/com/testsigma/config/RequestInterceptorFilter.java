package com.testsigma.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Log4j2
public class RequestInterceptorFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
    throws IOException, ServletException {
    resetTenant();
    setRequestId();
    logRequest((HttpServletRequest) servletRequest);
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
  }

  private void setRequestId() {
    String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
    ThreadContext.put("X-Request-Id", uuid);
  }

  private void resetTenant() {
    ThreadContext.put("X-Request-Id", "");
  }

  private void logRequest(HttpServletRequest request) {
    if (request != null) {
      String requestURI = request.getRequestURI();
      String requestQuery = ObjectUtils.defaultIfNull(request.getQueryString(), "");
      String output = requestURI;
      if (StringUtils.isNoneEmpty(requestQuery)) {
        output = output + "?" + requestQuery;
      }
      log.info(String.format("Request [%s %s]", request.getMethod(), output));
    }
  }
}
