package com.testsigma.service;

import com.testsigma.model.AuthUser;
import org.apache.logging.log4j.ThreadContext;

public class CurrentUserService {
  private static final ThreadLocal<AuthUser> CURRENT_USER = new ThreadLocal<>();

  public static synchronized AuthUser getCurrentUser() {
    return CURRENT_USER.get();
  }

  public static synchronized void setCurrentUser(AuthUser authUser) {
    CURRENT_USER.set(authUser);
    ThreadContext.put("CURRENT_USER", authUser.getUuid());
  }
}
