package com.serhat.order.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public record OrderRequest(
         List<String> productCodes,
         List<Integer> quantities,
        Integer addressId
) {

}
