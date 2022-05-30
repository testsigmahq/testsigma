package com.testsigma.exception;

public class InvalidStorageCredentialsException extends TestsigmaWebException {

    public InvalidStorageCredentialsException(String errorCode) {
        super(errorCode);
    }

    public InvalidStorageCredentialsException(String errorCode,String msg) {
        super(errorCode,msg);
    }

}

