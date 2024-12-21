package com.serhat.order.dto.object;

import com.serhat.order.entity.AddressType;
import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        Integer addressId,

        String street,

        String city,

        String state,

        String country,

        String postalCode,
        AddressType addressType,
        String description
) {
}
