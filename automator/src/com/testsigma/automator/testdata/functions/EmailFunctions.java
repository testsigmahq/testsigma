package com.testsigma.automator.testdata.functions;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import org.apache.commons.lang3.RandomStringUtils;

public class EmailFunctions {

  Internet domain = null;

  public EmailFunctions() {
    domain = new Faker().internet();
  }

  public String userName(int length) {
    return RandomStringUtils.randomAlphabetic(length).toLowerCase();

  }

  public String randomAlphanumaricEmail(int length) {
    String generateString = RandomStringUtils.randomAlphanumeric(length).toLowerCase();
    return new StringBuffer().append(generateString).append("@").append(domain.domainName()).toString();

  }

  public String randomAlphanumaricEmail(int length, String domain) {
    String generateString = RandomStringUtils.randomAlphanumeric(length).toLowerCase();
    return new StringBuffer().append(generateString).append("@").append(domain).toString();

  }

  public String randomEmail(int length, String domain) {
    return new StringBuffer().append(userName(length)).append("@").append(domain).toString();

  }

  public String randomEmail(int length) {
    String generateString = RandomStringUtils.randomAlphabetic(length).toLowerCase();
    return new StringBuffer().append(generateString).append("@").append(domain.domainName()).toString();
  }

}
