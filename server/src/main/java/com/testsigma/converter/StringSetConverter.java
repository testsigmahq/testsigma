/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class StringSetConverter extends SetConverter<String> {
}
