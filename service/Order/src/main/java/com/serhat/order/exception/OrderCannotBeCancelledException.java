package com.serhat.order.exception;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String s) {
        super(s);
    }
}
