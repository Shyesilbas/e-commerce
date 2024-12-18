package com.serhat.customer.dto.object;

import com.serhat.customer.entity.MembershipPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerDTO(
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
