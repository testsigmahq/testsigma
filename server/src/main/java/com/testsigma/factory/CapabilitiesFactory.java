package com.testsigma.factory;

import com.testsigma.model.WorkspaceType;
import com.testsigma.model.Browsers;
import com.testsigma.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class CapabilitiesFactory {
  @Autowired
  private final WebApplicationContext context;

  public CapabilitiesFactory(WebApplicationContext context) {
    this.context = context;
  }

  public Capabilities capabilities(WorkspaceType WorkspaceType, Browsers browser) {
    ChromeCapabilities chromeCapabilities = (ChromeCapabilities) context.getBean("chromeCapabilities");
    AndroidCapabilities androidCapabilities = (AndroidCapabilities) context.getBean("androidCapabilities");
    IosCapabilities iosCapabilities = (IosCapabilities) context.getBean("iosCapabilities");
    MobileWebCapabilities mobileWebCapabilities = (MobileWebCapabilities) context.getBean("mobileWebCapabilities");
    FirefoxCapabilities firefoxCapabilities = (FirefoxCapabilities) context.getBean("firefoxCapabilities");
    SafariCapabilities safariCapabilities = (SafariCapabilities) context.getBean("safariCapabilities");
    EdgeCapabilities edgeCapabilities = (EdgeCapabilities) context.getBean("edgeCapabilities");
    switch (WorkspaceType) {
      case WebApplication:
        switch (browser) {
          case GoogleChrome:
            return chromeCapabilities;
          case MozillaFirefox:
            return firefoxCapabilities;
          case Safari:
            return safariCapabilities;
          case MicrosoftEdge:
            return edgeCapabilities;
        }
      case IOSNative:
        return iosCapabilities;
      case AndroidNative:
        return androidCapabilities;
      case MobileWeb:
        return mobileWebCapabilities;
    }
    return null;
  }
}
