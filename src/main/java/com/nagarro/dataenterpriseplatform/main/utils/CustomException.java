package com.nagarro.dataenterpriseplatform.main.utils;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception
{
    private HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus, String errorReason, String message) {
        super(errorReason + message);
    }
}
