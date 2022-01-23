package com.testsigma.automator.testdata.functions;

import java.util.List;

public class RandomStringFunctions {
  public String randomStringFromGivenCharacters(int Stringlength, List<String> list) {
    String randomstring = "";
    for (int i = 0; i < Stringlength; i++) {
      int rnum = (int) Math.floor(Math.random() * list.size());
      randomstring += list.get(rnum);
    }
    return randomstring;
  }
}
