package com.jpmc.admin_service.mapper;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.enums.Status;
import com.jpmc.admin_service.model.Admin;

public class ToAdminMapper {
    public static Admin toAdmin(AddRequestDto addRequestDto) {
        Admin admin=new Admin();
        admin.setEmail(addRequestDto.getEmail());
        admin.setRequestedRole(addRequestDto.getRequestedRole());
        admin.setStatus(Status.PENDING);
        admin.setPermissionRequestId(addRequestDto.getPermissionRequestId());
        return admin;
    }
}
