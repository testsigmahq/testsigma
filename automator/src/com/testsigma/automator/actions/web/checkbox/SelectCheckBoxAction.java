package com.testsigma.automator.actions.web.checkbox;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SelectCheckBoxAction extends CheckBoxAction {
  @Override
  public void execute() throws Exception {
    check();
  }
}
