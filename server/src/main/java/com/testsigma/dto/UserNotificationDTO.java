package com.testsigma.dto;

import lombok.Data;

@Data
public class UserNotificationDTO {
  private String userName;
  private String domain;
  private String name;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String token;
  private String feedback;
  private Long rating;
  private Boolean switchedBackToLegacy;
  private String refererEmail;
}
