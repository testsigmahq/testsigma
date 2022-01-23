/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.constants;

import java.util.HashMap;
import java.util.Map;

public class IosProductType {
  public static final Map<String, String> productTypeMap = new HashMap<String, String>() {
    // Please refer https://gist.github.com/adamawolf/3048717 for latest device list
    {
      put("iPhone3,1", "iPhone 4");
      put("iPhone3,2", "iPhone 4");
      put("iPhone3,3", "iPhone 4");
      put("iPhone4,1", "iPhone 4s");
      put("iPhone5,1", "iPhone 5");
      put("iPhone5,2", "iPhone 5");
      put("iPhone5,3", "iPhone 5c");
      put("iPhone5,4", "iPhone 5c");
      put("iPhone6,1", "iPhone 5s");
      put("iPhone6,2", "iPhone 5s");
      put("iPhone7,1", "iPhone 6 Plus");
      put("iPhone7,2", "iPhone 6");
      put("iPhone8,1", "iPhone 6s");
      put("iPhone8,2", "iPhone 6s Plus");
      put("iPhone8,4", "iPhone SE");
      put("iPhone9,1", "iPhone 7");
      put("iPhone9,3", "iPhone 7");
      put("iPhone9,2", "iPhone 7 Plus");
      put("iPhone9,4", "iPhone 7 Plus");
      put("iPhone10,1", "iPhone 8");
      put("iPhone10,4", "iPhone 8");
      put("iPhone10,2", "iPhone 8 Plus");
      put("iPhone10,5", "iPhone 8 Plus");
      put("iPhone10,3", "iPhone X");
      put("iPhone10,6", "iPhone X");
      put("iPhone11,2", "iPhone XS");
      put("iPhone11,4", "iPhone XS Max");
      put("iPhone11,6", "iPhone XS Max");
      put("iPhone11,8", "iPhone XR");
      put("iPhone12,1", "iPhone 11");
      put("iPhone12,3", "iPhone 11 Pro");
      put("iPhone12,5", "iPhone 11 Pro Max");
      put("iPhone12,8", "iPhone SE 2nd Gen");
      put("iPhone13,1", "iPhone 12 Mini");
      put("iPhone13,2", "iPhone 12");
      put("iPhone13,3", "iPhone 12 Pro");
      put("iPhone13,4", "iPhone 12 Pro Max");
    }
  };

}
