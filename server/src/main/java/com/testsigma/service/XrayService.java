package com.testsigma.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.web.request.IntegrationsRequest;

public interface XrayService {

    JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException;
}
