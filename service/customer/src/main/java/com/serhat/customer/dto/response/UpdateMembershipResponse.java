package com.serhat.customer.dto.response;

import com.serhat.customer.entity.MembershipPlan;

public record UpdateMembershipResponse(
        String message,
        MembershipPlan newMembershipPlan
) {
}
