package com.testsigma.automator.testdata.functions;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

public class DomainFunctions {

  Name name = null;

  public DomainFunctions() {
    name = new Faker().name();
  }

  public String domainName(String domain) {

    return new StringBuffer().append(name.firstName()).append("@").append(domain).toString();

  }

}
