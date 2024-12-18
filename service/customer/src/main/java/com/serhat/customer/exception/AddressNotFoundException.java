package com.serhat.customer.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String s) {
        super(s);
    }
}
