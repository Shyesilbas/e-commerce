package com.serhat.customer.exception;

public class AccountAlreadyExistsForPhoneNumberException extends RuntimeException {
    public AccountAlreadyExistsForPhoneNumberException(String s) {
        super(s);
    }
}
