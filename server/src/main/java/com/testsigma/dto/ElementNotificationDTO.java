package com.testsigma.dto;
/*
 * *****************************************************************************
 *  Copyright (C) 2021 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import lombok.Data;

@Data
public class ElementNotificationDTO {

  private Long id;

  private String name;

  private Long assignee;

  private Long reviewedBy;

  private UserNotificationDTO from;

  private UserNotificationDTO to;

  private String comments;

  private String url;

  private String submittedFromUrl;

  private boolean testCaseUrl;

  private boolean testCaseResultUrl;

  private ElementNotificationDTO elementNotificationDTO;

  private String screenName;

  private String screenNameUrl;

  private String screenUrl;

}
