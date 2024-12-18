package com.serhat.customer.controller;

import com.serhat.customer.dto.request.CreateCustomerRequest;
import com.serhat.customer.dto.request.UpdateEmailRequest;
import com.serhat.customer.dto.request.UpdatePhoneNumberRequest;
import com.serhat.customer.dto.response.CreateCustomerResponse;
import com.serhat.customer.dto.response.UpdateEmailResponse;
import com.serhat.customer.dto.response.UpdatePhoneNumberResponse;
import com.serhat.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/updatePhone")
    public ResponseEntity<UpdatePhoneNumberResponse> updatePhone (Principal p , @Valid @RequestBody UpdatePhoneNumberRequest request ){
        return ResponseEntity.ok(customerService.updatePhoneNumber(p,request));
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<UpdateEmailResponse> updateEmail (Principal p , @Valid @RequestBody UpdateEmailRequest request ){
        return ResponseEntity.ok(customerService.updateEmail(p,request));
    }

    @GetMapping("/testUrl")
    public String test(){
        return "Test ,if you see this , it's a secured app";
    }

}
