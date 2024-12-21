package com.serhat.order.client;

import com.serhat.order.dto.object.AddressDTO;
import com.serhat.order.dto.object.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@FeignClient(name = "customer-service",url = "http://localhost:8070/api/customers")
public interface CustomerClient {

    @GetMapping("/info")
    CustomerDto customerInfo(@RequestHeader("Authorization") String authHeader);


    @GetMapping("/address")
    List<AddressDTO> addressInfoById(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/address/{addressId}")
    AddressDTO addressInfo(@PathVariable Integer addressId,@RequestHeader("Authorization") String authHeader);
    @PutMapping("/updateTotalOrders")
    void updateTotalOrders(@RequestParam int customerId, @RequestParam int increment ,@RequestHeader("Authorization") String authHeader);
}
