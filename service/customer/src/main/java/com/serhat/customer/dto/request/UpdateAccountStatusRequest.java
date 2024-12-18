package com.serhat.customer.dto.request;

import com.serhat.customer.entity.AccountStatus;

public record UpdateAccountStatusRequest(
        AccountStatus accountStatus
) {
}
