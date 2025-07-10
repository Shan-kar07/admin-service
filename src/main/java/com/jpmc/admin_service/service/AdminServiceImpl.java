package com.jpmc.admin_service.service;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.dto.NotificationDto;
import com.jpmc.admin_service.dto.RequestStatusUpdateDto;
import com.jpmc.admin_service.dto.UserRoleUpdateDto;
import com.jpmc.admin_service.enums.RequestStatus;
import com.jpmc.admin_service.enums.Status;
import com.jpmc.admin_service.exception.RequestNotPendingException;
import com.jpmc.admin_service.exception.SignupRequestNotFoundException;
import com.jpmc.admin_service.mapper.ToAdminMapper;
import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.repository.AdminRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Implements AdminService: manages signup-request lifecycle and cross-service calls.
 */
@AllArgsConstructor
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<Admin> listPendingRequests() {
        log.info("Fetching pending requests from DB");
        return adminRepository.findByStatus(Status.PENDING);
    }

    @Override
    public List<Admin> listAllRequests() {
        log.info("Fetching all requests from DB");
        return adminRepository.findAll();
    }

    @Override
    public List<Admin> listApprovedRequests() {
        log.info("Fetching approved requests from DB");
        return adminRepository.findByStatus(Status.APPROVED);
    }

    @Override
    public List<Admin> listRejectedRequests() {
        log.info("Fetching rejected requests from DB");
        return adminRepository.findByStatus(Status.REJECTED);
    }

    @Override
    @Transactional
    public Admin createSignupRequest(AddRequestDto dto) {
        log.info("Creating signup request for email={}", dto.getEmail());
        Admin entity = ToAdminMapper.toAdmin(dto);
        Admin saved = adminRepository.save(entity);
        log.info("Signup request saved with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public String approveRequest(Long id)
            throws SignupRequestNotFoundException, RequestNotPendingException {
        log.info("Starting approval workflow for id={}", id);
        Admin req = adminRepository.findById(id)
                .orElseThrow(() -> new SignupRequestNotFoundException(id));

        if (req.getStatus() != Status.PENDING) {
            throw new RequestNotPendingException(id, req.getStatus().name());
        }

        // mark approved in our DB
        req.setStatus(Status.APPROVED);
        adminRepository.save(req);
        log.info("Request id={} marked APPROVED in DB", id);

        // cross-service calls
        callUpdateRole(req);
        callUpdatePermissionStatus(req, RequestStatus.APPROVED);
        callNotify(req, "approved");

        log.info("Approval workflow completed for id={}", id);
        return "Request ID " + id + " approved successfully.";
    }

    @Override
    @Transactional
    public String rejectRequest(Long id)
            throws SignupRequestNotFoundException, RequestNotPendingException {
        log.info("Starting rejection workflow for id={}", id);
        Admin req = adminRepository.findById(id)
                .orElseThrow(() -> new SignupRequestNotFoundException(id));

        if (req.getStatus() != Status.PENDING) {
            throw new RequestNotPendingException(id, req.getStatus().name());
        }

        // mark rejected in our DB
        req.setStatus(Status.REJECTED);
        adminRepository.save(req);
        log.info("Request id={} marked REJECTED in DB", id);

        // cross-service calls
        callUpdatePermissionStatus(req, RequestStatus.DENIED);
        callNotify(req, "rejected");

        log.info("Rejection workflow completed for id={}", id);
        return "Request ID " + id + " rejected successfully.";
    }

    // helper to update user role in UserService
    private void callUpdateRole(Admin req) {
        log.debug("Calling user-service to update role for email={}", req.getEmail());
        UserRoleUpdateDto dto = new UserRoleUpdateDto();
        dto.setEmail(req.getEmail());
        dto.setUpdateRole(req.getRequestedRole());
        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/admin/update")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    // helper to update permission request status in UserService
    private void callUpdatePermissionStatus(Admin req, RequestStatus status) {
        log.debug("Calling user-service to update permission status for requestId={}",
                req.getPermissionRequestId());
        RequestStatusUpdateDto dto = new RequestStatusUpdateDto();
        dto.setRequestId(req.getPermissionRequestId());
        dto.setStatus(status);
        webClientBuilder.build()
                .put()
                .uri("http://localhost:9093/user/requests/update-status")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    // helper to send notification via UserService
    private void callNotify(Admin req, String action) {
        log.debug("Calling user-service to notify email={} of {}", req.getEmail(), action);
        NotificationDto dto = new NotificationDto();
        dto.setEmail(req.getEmail());
        dto.setMessage("Your request for role " + req.getRequestedRole() +
                " has been " + action + ".");
        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/notification")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
