package com.serhat.order.dto.responses;

import com.serhat.order.dto.object.AddressDTO;

public record UpdateDeliveryAddressResponse(
        String message,
        AddressDTO updatedAddress,
        String warning
) {
}
