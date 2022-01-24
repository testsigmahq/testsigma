package com.testsigma.automator.testdata.functions;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

public class Sample_EnvironmentParameters {

  Name name = null;

  public Sample_EnvironmentParameters() {
    name = new Faker().name();
  }

  public String uniqueUsername() {
    return name.username() + System.currentTimeMillis();
  }

}
