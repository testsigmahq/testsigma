package com.testsigma.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeprecatedActionMapper {
  public static HashMap<Integer, String> getWebWaitMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(7, "visible");
    map.put(8, "selected");
    map.put(10, "not selected");
    map.put(11, "enabled");
    map.put(12, "disabled");
    map.put(13, "clickable");
    map.put(18, "not visible");
    return map;
  }

  public static HashMap<Integer, String> getWebVerifyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(54, "checked");
    map.put(72, "unchecked");
    map.put(1078, "present");
    map.put(69, "not present");
    map.put(73, "enabled");
    map.put(75, "disabled");
    map.put(74, "displayed");
    map.put(1052, "not displayed");
    return map;
  }

  public static HashMap<Integer, String> getWebClickOnButtonMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(104, "Refresh");
    map.put(105, "Forward");
    map.put(106, "Back");
    return map;
  }

  public static HashMap<Integer, String> getWebScrollInsideElementMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(1058, "top");
    map.put(1059, "bottom");
    return map;
  }

  public static HashMap<Integer, String> getWebScrollToElementMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(81, "up to");
    map.put(83, "down to");
    map.put(82, "to");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebVerifyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10053, "checked");
    map.put(10054, "unchecked");
    map.put(10188, "present");
    map.put(10039, "not present");
    map.put(10051, "enabled");
    map.put(10052, "disabled");
    map.put(10040, "displayed");
    map.put(10159, "not displayed");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebWaitMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10060, "visible");
    map.put(10062, "selected");
    map.put(10063, "not selected");
    map.put(10065, "enabled");
    map.put(10066, "disabled");
    map.put(10077, "clickable");
    map.put(10076, "not visible");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebTapOnAlertMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10010, "OK");
    map.put(10009, "Cancel");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebSwipeMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10174, "bottom to top");
    map.put(10175, "top to bottom");
    map.put(10176, "middle to top");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebTapOnKeyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10109, "space");
    map.put(10110, "backspace");
    map.put(10111, "enter");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebTapOnButtonMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10021, "Refresh");
    map.put(10020, "Forward");
    map.put(10019, "Back");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebScrollInsideElementMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10162, "top");
    map.put(10163, "bottom");
    return map;
  }

  public static HashMap<Integer, String> getMobileWebScrollToElementMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(10100, "up to");
    map.put(10098, "down to");
    map.put(10099, "to");
    return map;
  }

  public static HashMap<Integer, String> getAndroidWaitMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20045, "visible");
    map.put(20047, "selected");
    map.put(20048, "not selected");
    map.put(20050, "enabled");
    map.put(20051, "disabled");
    map.put(20061, "clickable");
    map.put(20060, "not visible");
    return map;
  }

  public static HashMap<Integer, String> getAndroidVerifyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20149, "present");
    map.put(20041, "not present");
    map.put(20032, "enabled");
    map.put(20034, "disabled");
    map.put(20040, "displayed");
    return map;
  }

  public static HashMap<Integer, String> getAndroidTapOnAlertMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20024, "OK");
    map.put(20022, "Cancel");
    return map;
  }

  public static HashMap<Integer, String> getAndroidSwipeFromMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20093, "left to right");
    map.put(20094, "right to left");
    map.put(20095, "middle to left");
    map.put(20096, "middle to right");
    map.put(20097, "left to middle");
    map.put(20098, "right to middle");
    map.put(20099, "top to bottom");
    map.put(20100, "bottom to top");
    map.put(20101, "top to middle");
    map.put(20103, "middle to top");
    map.put(20102, "bottom to middle");
    map.put(20104, "middle to bottom");
    return map;
  }

  public static HashMap<Integer, String> getAndroidSwipeToMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20136, "top");
    map.put(20137, "bottom");
    map.put(20135, "left");
    map.put(20134, "right");

    return map;
  }

  public static HashMap<Integer, String> getAndroidTapOnKeyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20106, "space");
    map.put(20107, "backspace");
    map.put(20108, "enter");
    map.put(20126, "search");
    return map;
  }

  public static HashMap<Integer, String> getAndroidEnableSwitchMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(20116, "Enable");
    map.put(20117, "Disable");
    return map;
  }

  public static HashMap<Integer, String> getIOSWaitMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30045, "visible");
    map.put(30050, "enabled");
    map.put(30051, "disabled");
    map.put(30061, "clickable");
    map.put(30060, "not visible");
    return map;
  }

  public static HashMap<Integer, String> getIOSVerifyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30143, "present");
    map.put(30041, "not present");
    map.put(30032, "enabled");
    map.put(30034, "disabled");
    map.put(30040, "displayed");
    return map;
  }

  public static HashMap<Integer, String> getIOSTapOnAlertMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30024, "OK");
    map.put(30022, "Cancel");
    return map;
  }

  public static HashMap<Integer, String> getIOSSwipeFromMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30092, "left to right");
    map.put(30093, "right to left");
    map.put(30094, "middle to left");
    map.put(30095, "middle to right");
    map.put(30096, "left to middle");
    map.put(30097, "right to middle");
    map.put(30098, "top to bottom");
    map.put(30099, "bottom to top");
    map.put(30100, "top to middle");
    map.put(30102, "middle to top");
    map.put(30101, "bottom to middle");
    map.put(30103, "middle to bottom");
    return map;
  }

  public static HashMap<Integer, String> getIOSSwipeToMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30126, "top");
    map.put(30127, "bottom");
    map.put(30125, "left");
    map.put(30124, "right");

    return map;
  }

  public static HashMap<Integer, String> getIOSTapOnKeyMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30105, "space");
    map.put(30106, "backspace");
    map.put(30107, "enter");
    return map;
  }

  public static HashMap<Integer, String> getIOSEnableSwitchMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30109, "Enable");
    map.put(30110, "Disable");
    return map;
  }

  public static HashMap<Integer, String> getIOSWIFISwitchMap(){
    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(30133, "Enable");
    map.put(30132, "Disable");
    return map;
  }

  public static List<Integer> getAllDeprecatedActionIds(){
    HashMap<Integer, String> totalHash = new HashMap<>(getWebVerifyMap());
    totalHash.putAll(getWebWaitMap());
    totalHash.putAll(getWebClickOnButtonMap());
    totalHash.putAll(getWebScrollToElementMap());
    totalHash.putAll(getWebScrollInsideElementMap());

    totalHash.putAll(getMobileWebWaitMap());
    totalHash.putAll(getMobileWebVerifyMap());
    totalHash.putAll(getMobileWebSwipeMap());
    totalHash.putAll(getMobileWebTapOnAlertMap());
    totalHash.putAll(getMobileWebScrollToElementMap());
    totalHash.putAll(getMobileWebScrollInsideElementMap());
    totalHash.putAll(getMobileWebTapOnKeyMap());
    totalHash.putAll(getMobileWebTapOnButtonMap());

    totalHash.putAll(getAndroidEnableSwitchMap());
    totalHash.putAll(getAndroidSwipeFromMap());
    totalHash.putAll(getAndroidSwipeToMap());
    totalHash.putAll(getAndroidTapOnAlertMap());
    totalHash.putAll(getAndroidVerifyMap());
    totalHash.putAll(getAndroidTapOnKeyMap());
    totalHash.putAll(getAndroidWaitMap());

    totalHash.putAll(getIOSEnableSwitchMap());
    totalHash.putAll(getIOSSwipeFromMap());
    totalHash.putAll(getIOSSwipeToMap());
    totalHash.putAll(getIOSTapOnAlertMap());
    totalHash.putAll(getIOSVerifyMap());
    totalHash.putAll(getIOSTapOnKeyMap());
    totalHash.putAll(getIOSWaitMap());
    totalHash.putAll(getIOSWIFISwitchMap());

    return new ArrayList<>(totalHash.keySet());
  }


}
