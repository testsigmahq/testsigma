package com.testsigma.agent.exception;

public class ZipFailedException extends Exception {

    public ZipFailedException(String description, Throwable e) {
        super(description, e);
    }
}
