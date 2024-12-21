package com.serhat.order.dto.object;

import com.serhat.order.entity.MembershipPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerDto(
        Integer customerId,
        String name,
        String surname,
        String email,
        MembershipPlan membershipPlan,
        int total_orders,
        LocalDateTime joinDate,
        LocalDate birthdate,
        List<AddressDTO> addresses
) {
}
