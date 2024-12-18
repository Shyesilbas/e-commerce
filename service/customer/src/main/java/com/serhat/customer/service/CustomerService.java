package com.serhat.customer.service;

import com.serhat.customer.dto.request.CreateCustomerRequest;
import com.serhat.customer.dto.request.UpdateEmailRequest;
import com.serhat.customer.dto.request.UpdatePhoneNumberRequest;
import com.serhat.customer.dto.response.CreateCustomerResponse;
import com.serhat.customer.dto.response.UpdateEmailResponse;
import com.serhat.customer.dto.response.UpdatePhoneNumberResponse;
import com.serhat.customer.entity.*;
import com.serhat.customer.exception.*;
import com.serhat.customer.repository.AddressRepository;
import com.serhat.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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

    @Transactional
    public UpdatePhoneNumberResponse updatePhoneNumber(Principal principal, UpdatePhoneNumberRequest request) {
        String email = principal.getName();
        log.info("Customer : "+email);
        Customer customer = customerRepository.findByLoweCaseEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Please check your credentials."));

        log.info("Customer '{}' with email '{}' is changing their phone number from '{}' to '{}'",
                customer.getName(), email, customer.getPhone(), request.newPhoneNumber());

        String currentPhoneNumber = customer.getPhone();
        String requestedPhoneNumber = request.newPhoneNumber();

        if (currentPhoneNumber.equals(requestedPhoneNumber)) {
            throw new SameRequestException("The requested phone number is the same as the current one.");
        }
        boolean isRequestedPhoneExists = customerRepository.findByPhone(requestedPhoneNumber).isPresent();
        if (isRequestedPhoneExists) {
            throw new AccountAlreadyExistsForPhoneNumberException(
                    String.format("An account already exists for the phone number: %s", requestedPhoneNumber));
        }
        customer.setPhone(requestedPhoneNumber);
        customerRepository.save(customer);

        log.info("Phone number successfully updated to '{}'", requestedPhoneNumber);

        return new UpdatePhoneNumberResponse(
                "Phone number updated successfully.",
                requestedPhoneNumber
        );
    }

    @Transactional
    public UpdateEmailResponse updateEmail(Principal principal, UpdateEmailRequest request) {
        String email = principal.getName();
        Customer customer = customerRepository.findByLoweCaseEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. Please check your credentials."));

        log.info("Customer '{}' with email '{}' is changing their email  to '{}'",
                customer.getName(), email, request.newEmail());

        String currentEmail = customer.getEmail();
        String requestedEmail = request.newEmail();

        if (currentEmail.equals(requestedEmail)) {
            throw new SameRequestException("The requested Email is the same as the current one.");
        }
        boolean isRequestedEmailExists = customerRepository.findByEmail(requestedEmail).isPresent();
        if (isRequestedEmailExists) {
            throw new AccountAlreadyExistsForPhoneNumberException(
                    String.format("An account already exists for the Email: %s", requestedEmail));
        }
        customer.setEmail(requestedEmail);
        customerRepository.save(customer);
        keycloakCustomerService.updateKeycloakEmail(currentEmail,requestedEmail);

        log.info("Email successfully updated to '{}'", requestedEmail);
        log.info("Email updated on Keycloak successfully.");

        return new UpdateEmailResponse(
                "Email updated successfully.",
                requestedEmail
        );
    }



}
