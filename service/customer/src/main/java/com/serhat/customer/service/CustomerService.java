package com.serhat.customer.service;

import com.serhat.customer.dto.request.CreateCustomerRequest;
import com.serhat.customer.dto.response.CreateCustomerResponse;
import com.serhat.customer.entity.*;
import com.serhat.customer.exception.EmailExistsException;
import com.serhat.customer.exception.PhoneNumberExistsException;
import com.serhat.customer.repository.AddressRepository;
import com.serhat.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final KeycloakCustomerService keycloakCustomerService;

    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request){
        boolean isPhoneExists = customerRepository.findByPhone(request.phone()).isPresent();
        if(isPhoneExists){
            log.warn("Phone {} already exists", request.phone());
            throw new PhoneNumberExistsException("Account found for the phone number : "+request.phone());
        }
        boolean isEmailExists = customerRepository.findByEmail(request.email()).isPresent();
        if(isEmailExists){
            log.warn("Email {} already exists", request.email());
            throw new EmailExistsException("Account found for the email : "+request.email());
        }


        Customer customer = Customer.builder()
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .password(request.password())
                .phone(request.phone())
                .membershipPlan(MembershipPlan.STANDARD)
                .birthdate(request.birthdate())
                .accountStatus(AccountStatus.ACTIVE)
                .totalOrders(0)
                .role(Role.CUSTOMER)
                .joinDate(LocalDateTime.now())

                .build();

        List<Address> addresses = request.addresses()
                .stream()
                .map(addressDTO -> Address.builder()
                        .street(addressDTO.street())
                        .city(addressDTO.city())
                        .state(addressDTO.state())
                        .country(addressDTO.country())
                        .postalCode(addressDTO.postalCode())
                        .addressType(addressDTO.addressType())
                        .description(addressDTO.description())
                        .customer(customer)
                        .build())
                .toList();
        customer.setAddresses(addresses);

        customerRepository.save(customer);
        keycloakCustomerService.createKeycloakUser(customer);
        log.info("Customer created with name: {}, email: {}", customer.getName(), customer.getEmail());

        return new CreateCustomerResponse(
                "Account Created Successfully , Welcome",
                customer.getEmail(),
                customer.getName(),
                customer.getSurname()
        );
    }




}
