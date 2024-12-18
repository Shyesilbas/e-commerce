package com.serhat.customer.exception;

public class SameRequestException extends RuntimeException{
    public SameRequestException(String s) {
        super(s);
    }
}
