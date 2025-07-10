package com.jpmc.admin_service.dto;

import com.jpmc.admin_service.enums.RequestStatus;
import lombok.Data;

@Data
public class RequestStatusUpdateDto {
    private Long requestId;
    private RequestStatus status;
}
