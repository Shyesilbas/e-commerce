package com.serhat.customer.controller;

import com.serhat.customer.dto.request.CreateCustomerRequest;
import com.serhat.customer.dto.response.CreateCustomerResponse;
import com.serhat.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/testUrl")
    public String test(){
        return "Test , Secured url?";
    }

}
