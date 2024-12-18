package com.serhat.customer.dto.request;

import com.serhat.customer.entity.MembershipPlan;

public record UpdateMembershipRequest(
        MembershipPlan newMembershipPlan
) {
}
