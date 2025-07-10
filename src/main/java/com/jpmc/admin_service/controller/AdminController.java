package com.jpmc.admin_service.controller;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.exception.RequestNotPendingException;
import com.jpmc.admin_service.exception.SignupRequestNotFoundException;
import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes REST endpoints for admin to manage signup requests.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/requests")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/addrequest")
    public ResponseEntity<Admin> addRequest(@RequestBody AddRequestDto dto) {
        log.info("Received new signup request for email={}", dto.getEmail());
        Admin created = adminService.createSignupRequest(dto);
        log.info("Created signup request id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Admin>> listPending() {
        log.info("Listing all pending signup requests");
        return ResponseEntity.ok(adminService.listPendingRequests());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Admin>> listAll() {
        log.info("Listing all signup requests");
        return ResponseEntity.ok(adminService.listAllRequests());
    }

    @GetMapping("/approvedlist")
    public ResponseEntity<List<Admin>> listApproved() {
        log.info("Listing approved signup requests");
        return ResponseEntity.ok(adminService.listApprovedRequests());
    }

    @GetMapping("/rejectedlist")
    public ResponseEntity<List<Admin>> listRejected() {
        log.info("Listing rejected signup requests");
        return ResponseEntity.ok(adminService.listRejectedRequests());
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveRequest(
            @PathVariable Long id)
            throws SignupRequestNotFoundException, RequestNotPendingException {
        log.info("Approving signup request id={}", id);
        String msg = adminService.approveRequest(id);
        log.info("Approval successful for id={}", id);
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectRequest(
            @PathVariable Long id)
            throws SignupRequestNotFoundException, RequestNotPendingException {
        log.info("Rejecting signup request id={}", id);
        String msg = adminService.rejectRequest(id);
        log.info("Rejection successful for id={}", id);
        return ResponseEntity.ok(msg);
    }
}
