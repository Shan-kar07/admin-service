package com.jpmc.admin_service.exception;

public class SignupRequestNotFoundException extends Exception {
    public SignupRequestNotFoundException(Long id) {
        super("Signup request with ID " + id + " not found.");
    }
}