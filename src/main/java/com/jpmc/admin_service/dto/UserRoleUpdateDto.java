package com.jpmc.admin_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRoleUpdateDto {
    private String email;
    private String updateRole;
}
