/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.config;

public interface URLConstants {

  String LOGIN_URL = "/login";

  String LOGOUT_URL = "/logout";

  String API_BASE_URL = "/api/v1";

  String AGENT_API_BASE_URL = "/api/agents";

  String SESSION_RESOURCE_URL = "/sessions";

  String OAUTH2_BASE_URL = "/login/oauth2";

  String AGENT_CERTIFICATE_URL = AGENT_API_BASE_URL + "/certificate";

  String ASSETS_URL = "/assets/**";

  String ALL_SUB_URLS = "/**";

  String ALL_URLS = "/**/*";

  String PRESIGNED_BASE_URL = "/presigned/storage";

  String TESTSIGMA_OS_PUBLIC_CERTIFICATE_URL = "/api_public/agents/certificate";

  String TESTSIGMA_OS_PUBLIC_IOS_IMAGE_FILES_URL = "/api_public/agents/ios_images";

  String TESTSIGMA_OS_PUBLIC_WDA_URL = "/api_public/agents/wda";

  String TESTSIGMA_OS_PUBLIC_SERVERS_URL = "/api_public/servers";

  String TESTSIGMA_OS_TESTSIGMA_LAB_IP_URL = "/api_public/testsigma_lab_ip";

  String VISUAL_API_URL = "/api/visual/imageanalysis";
}
