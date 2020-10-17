package com.cryptofacilities.interview;

public class OrderNotFoundException extends Exception {
    public OrderNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}