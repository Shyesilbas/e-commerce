package com.serhat.customer.dto.response;

import com.serhat.customer.entity.AccountStatus;

public record UpdateAccountStatusResponse(
        String message,
        AccountStatus accountStatus
) {
}
