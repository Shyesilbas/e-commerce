package com.serhat.order.controller;

import com.serhat.order.dto.object.OrderDTO;
import com.serhat.order.dto.requests.OrderRequest;
import com.serhat.order.dto.requests.PlaceOrderRequestDTO;
import com.serhat.order.dto.responses.CancelOrderResponse;
import com.serhat.order.dto.responses.OrderDetailsResponse;
import com.serhat.order.dto.responses.OrderHistoryResponse;
import com.serhat.order.dto.responses.OrderPlacedResponse;
import com.serhat.order.entity.Order;
import com.serhat.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> orderHistory(Principal p){
        return ResponseEntity.ok(orderService.orderHistory(p));
    }

    @GetMapping("/historyBetweenTimeRange")
    public ResponseEntity<List<OrderDetailsResponse>> getOrdersHistory(Principal p,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime to
    ){
        return ResponseEntity.ok(orderService.ordersBetweenTimeRange(p,from,to));
    }

    @GetMapping("/orderDetails")
    public ResponseEntity<OrderDetailsResponse> orderDetails(@RequestParam Integer id , Principal principal){
        return ResponseEntity.ok(orderService.orderDetails(principal,id));
    }

    @PutMapping("/cancelOrder")
    public ResponseEntity<CancelOrderResponse> cancelOrder (@RequestParam Integer id , Principal p){
        return ResponseEntity.ok(orderService.cancelOrder(p,id));
    }
}
