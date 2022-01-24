package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.exceptions.AutomatorException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class MobileWebDriver extends MobileDriver {

  public MobileWebDriver() {
    super();
  }

  @Override
  protected void setCommonCapabilities() throws AutomatorException {
    super.setCommonCapabilities();
  }

  @Override
  protected void setTestsigmaLabCapabilities() throws AutomatorException {
    super.setTestsigmaLabCapabilities();
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
  }
}
