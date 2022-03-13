package com.testsigma.automator.actions.mobile.android.app;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriverException;
import org.springframework.util.Assert;

public class OpenAppAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Application opened successfully using app package and activity name.";
  private static final String FAILURE_MESSAGE = "Unable to open the Application, please provide the valid package and activity name.<br>" +
    "Given Package Name: <b>\"%s\"</b> <br>Given Activity Name: <b>\"%s\"</b>";
  private static final String SEMICOLON_ERROR_MESSAGE = "Invalid AppName:AppPackage format provided in test data <b>\"%s\"</b>. <br> Example test data <b>AppPackage:AppActivity</b>";

  private String packageName;
  private String activityName;

  @Override
  protected void execute() throws Exception {

    Assert.isTrue(getTestData().contains(":"), String.format(SEMICOLON_ERROR_MESSAGE, getTestData()));
    String[] diff = getTestData().split(":");
    packageName = diff[0].trim();
    activityName = diff[1].trim();
    ((AndroidDriver) getDriver()).startActivity(new Activity(packageName, activityName));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  public void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof WebDriverException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, packageName, activityName));
    }
  }
}
