package com.serhat.product.exception;

public class SameQuantityRequestException extends RuntimeException {
    public SameQuantityRequestException(String s) {
        super(s);
    }
}
