package com.serhat.customer.controller;

import com.serhat.customer.dto.object.AddressDTO;
import com.serhat.customer.dto.object.CustomerDTO;
import com.serhat.customer.dto.request.*;
import com.serhat.customer.dto.response.*;
import com.serhat.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        return ResponseEntity.ok(customerService.createCustomer(request));
    }
    @PostMapping("/addAddress")
    public ResponseEntity<AddAddressResponse> addAddress(Principal principal , @Valid @RequestBody AddAddressRequest request){
        return ResponseEntity.ok(customerService.addAddress(principal,request));
    }

    @DeleteMapping("/deleteAddress")
    public ResponseEntity<String> deleteAddress(Principal principal , @RequestParam Integer addressId){
        return ResponseEntity.ok(customerService.deleteAddress(principal,addressId));
    }

    @PutMapping("/updatePhone")
    public ResponseEntity<UpdatePhoneNumberResponse> updatePhone (Principal p , @Valid @RequestBody UpdatePhoneNumberRequest request ){
        return ResponseEntity.ok(customerService.updatePhoneNumber(p,request));
    }

    @PutMapping("/updateEmail")
    public ResponseEntity<UpdateEmailResponse> updateEmail (Principal p , @Valid @RequestBody UpdateEmailRequest request ){
        return ResponseEntity.ok(customerService.updateEmail(p,request));
    }
    @PutMapping("/updateMembership")
    public ResponseEntity<UpdateMembershipResponse> updateEmail (Principal p , @Valid @RequestBody UpdateMembershipRequest request ){
        return ResponseEntity.ok(customerService.updateMembership(p,request));
    }
    @PutMapping("/updateAccountStatus")
    public ResponseEntity<UpdateAccountStatusResponse> updateEmail (Principal p , @Valid @RequestBody UpdateAccountStatusRequest request ){
        return ResponseEntity.ok(customerService.updateAccountStatus(p,request));
    }

    @GetMapping("/info")
    public ResponseEntity<CustomerDTO> customerInformation(Principal p){
        return ResponseEntity.ok(customerService.customerInformation(p));
    }
    @GetMapping("/address")
    public ResponseEntity<List<AddressDTO>> displayAddresses(Principal p){
        return ResponseEntity.ok(customerService.displayAddresses(p));
    }

    @GetMapping("/testUrl")
    public String test(){
        return "Test ,if you see this , it's a secured app";
    }

}
