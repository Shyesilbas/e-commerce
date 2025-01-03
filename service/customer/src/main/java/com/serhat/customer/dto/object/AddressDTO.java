package com.serhat.customer.dto.object;

import com.serhat.customer.entity.AddressType;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record AddressDTO(
        Integer addressId,

        String street,

        String city,

        String state,

        String country,

        String postalCode,
        AddressType addressType,
        String description
) implements Serializable {
}
