package com.serhat.customer.dto.object;

import com.serhat.customer.entity.AddressType;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record AddressDTO(
        @NotBlank(message = "Street is required")
        String street,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "Country is required")
        String country,
        @NotBlank(message = "Postal code is required")
        String postalCode,
        @NotBlank(message = "Address type is required")
        AddressType addressType,
        @NotBlank(message = "Description is required")
        String description
) implements Serializable {
}
