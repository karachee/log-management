package com.karachee.lms.controllers.utils.exception;

/**
 * Created by yelena.solomonik on 11/21/2016.
 */
public class BadRequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }
}
