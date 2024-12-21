package com.serhat.order.controller;

import com.serhat.order.dto.object.OrderDTO;
import com.serhat.order.dto.requests.OrderRequest;
import com.serhat.order.dto.requests.PlaceOrderRequestDTO;
import com.serhat.order.dto.responses.OrderPlacedResponse;
import com.serhat.order.entity.Order;
import com.serhat.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderPlacedResponse> placeOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        return ResponseEntity.ok(orderService.placeOrder(principal,orderRequest));
    }

}
