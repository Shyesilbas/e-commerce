package com.serhat.customer.dto.request;

import com.serhat.customer.dto.object.AddressDTO;

public record UpdateAddressRequest(
        AddressDTO addressDTO
) {
}
