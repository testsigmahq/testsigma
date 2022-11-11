package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EntityType {
    TEST_CASE,
    TEST_SUITE,
    TEST_PLAN,
    RUN_RESULT,
    TEST_CASE_RESULT,
    TEST_SUITE_RESULT;
}
