package com.jpmc.admin_service.model;

import com.jpmc.admin_service.enums.Status;
import jakarta.persistence.*;
import lombok.Data; // While present, if you write getters/setters manually, @Data becomes redundant for them

@Entity
@Table(name = "signup_requests")
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String requestedRole;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long permissionRequestId;
}