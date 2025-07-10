package com.jpmc.admin_service.service;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.exception.RequestNotPendingException;
import com.jpmc.admin_service.exception.SignupRequestNotFoundException;
import com.jpmc.admin_service.model.Admin;

import java.util.List;

public interface AdminService {

    // Lists all pending signup requests
    List<Admin> listPendingRequests();

    // Allows admin to approve a signup request by ID
    String approveRequest(Long id) throws SignupRequestNotFoundException, RequestNotPendingException;

    // Allows admin to reject a signup request by ID
    String rejectRequest(Long id) throws SignupRequestNotFoundException, RequestNotPendingException;

    // Creates a new signup request (used internally or by event listener from UserService)
    Admin createSignupRequest(AddRequestDto addRequestDto);

    // Lists all signup requests regardless of status
    List<Admin> listAllRequests();

    List<Admin> listApprovedRequests();

    List<Admin> listRejectedRequests();
}