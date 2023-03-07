package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.Assert;

import java.time.Duration;

public class WaitUntilFileDownloadIsCompleteAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Download is completed";
  private static final String FAILURE_MESSAGE = "Download is not yet completed. Waited for <b>\"%s\"</b> seconds for download to complete";

  @Override
  public void execute() throws Exception {
    getDriver().navigate().to("chrome://downloads");
    try {
      String chromeJavaScript = "var tag = document.querySelector('downloads-manager').shadowRoot;" +
        "    var item_tags = tag.querySelectorAll('downloads-item');" +
        "    var item_tags_length = item_tags.length;" +
        "    var progress_lst = [];" +
        "    for(var i=0; i<item_tags_length; i++) {" +
        "        var intag = item_tags[i].shadowRoot;" +
        "        var progress_tag = intag.getElementById('progress');" +
        "        var progress = null;" +
        "        if(progress_tag && progress_tag.value < 100) {" +
        "             progress = progress_tag.value;" +
        "        }" +
        "        if(progress!=null) progress_lst.push(progress);" +
        "    }" +
        "    return progress_lst";
      //We create a custom wait with long sleep time. Since we are only allowing max of 120 secs for step level timeout(which
      // may not be sufficient for some downloads), we will be giving additional timeout here.
      WebDriverWait waiter = new WebDriverWait(getDriver(), Duration.ofSeconds(600), Duration.ofSeconds(5000));

      boolean isDownloadComplted = waiter.until(CustomExpectedConditions.downloadToBeCompletedInChrome(chromeJavaScript));
      Assert.isTrue(isDownloadComplted, String.format(FAILURE_MESSAGE, 600));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, 600), (Exception) e.getCause());
    } finally {
      getDriver().navigate().back();
    }
  }
}
