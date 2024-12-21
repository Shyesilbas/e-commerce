package com.serhat.order.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequestDTO {
    private List<String> productCodes;
    private List<Integer> quantities;
}
