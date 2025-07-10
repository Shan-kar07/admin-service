package com.jpmc.admin_service.exception;

public class RequestNotPendingException extends Exception {
    public RequestNotPendingException(Long id, String currentStatus) {
        super("Request ID " + id + " is not pending. Current status: " + currentStatus);
    }
}