package com.serhat.order.dto.object;

import com.serhat.order.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Integer id;
    private int customerId;
    private int quantity;
    private BigDecimal totalPrice;
    private Status status;
}
