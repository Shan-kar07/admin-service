package com.jpmc.admin_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddRequestDto {
    private String email;
    private String requestedRole;
    private Long permissionRequestId;
}
