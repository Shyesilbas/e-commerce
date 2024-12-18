package com.serhat.customer.service;

import com.serhat.customer.dto.object.AddressDTO;
import com.serhat.customer.dto.object.CustomerDTO;
import com.serhat.customer.dto.request.*;
import com.serhat.customer.dto.response.*;
import com.serhat.customer.entity.*;
import com.serhat.customer.exception.*;
import com.serhat.customer.repository.AddressRepository;
import com.serhat.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;


    public Customer findCustomer(Principal principal){
        String email = principal.getName();
        log.info("Customer : "+email);
        return customerRepository.findByLoweCaseEmail(email)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found. Please check your credentials."));
    }

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
        Customer customer = findCustomer(principal);

        log.info("Customer '{}' with email '{}' is changing their phone number from '{}' to '{}'",
                customer.getName(), customer.getEmail(), customer.getPhone(), request.newPhoneNumber());

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
       Customer customer = findCustomer(principal);
        log.info("Customer '{}' with email '{}' is changing their email  to '{}'",
                customer.getName(), customer.getEmail(), request.newEmail());

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

    @Transactional
    public UpdateMembershipResponse updateMembership(Principal principal, UpdateMembershipRequest request) {
        Customer customer = findCustomer(principal);

        log.info("Customer '{}' with email '{}' is changing their Membership plan  to '{}'",
                customer.getName(), customer.getEmail(), request.newMembershipPlan());

        MembershipPlan currentMembershipPlan = customer.getMembershipPlan();
        MembershipPlan requestedMembershipPlan = request.newMembershipPlan();

        if (currentMembershipPlan.equals(requestedMembershipPlan)) {
            throw new SameRequestException("The requested Membership Plan is the same as the current one.");
        }

        customer.setMembershipPlan(requestedMembershipPlan);
        customerRepository.save(customer);

        log.info("Membership Plan successfully updated to '{}'", requestedMembershipPlan);

        return new UpdateMembershipResponse(
                "Email updated successfully.",
                requestedMembershipPlan
        );
    }


    @Transactional
    public UpdateAddressResponse updateAddress(Principal principal, Integer addressId, UpdateAddressRequest request) {
        Customer customer = findCustomer(principal);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found by id: " + addressId));

        if (!customer.getAddresses().contains(address)) {
            throw new MissmatchException("This address does not belong to the logged-in customer.");
        }

        address.setStreet(request.addressDTO().street());
        address.setCity(request.addressDTO().city());
        address.setState(request.addressDTO().state());
        address.setCountry(request.addressDTO().country());
        address.setPostalCode(request.addressDTO().postalCode());
        address.setAddressType(request.addressDTO().addressType());
        address.setDescription(request.addressDTO().description());

        addressRepository.save(address);
        log.info("Address with id '{}' has been updated for customer '{}'", addressId, customer.getEmail());
        return new UpdateAddressResponse("Address updated successfully.", address.getDescription());
    }

    @Transactional
    public UpdatePasswordResponse updatePassword(Principal principal, UpdatePasswordRequest request) {
        Customer customer = findCustomer(principal);
        String currentPassword = customer.getPassword();
        String requestedPassword = request.newPassword();

        if (!currentPassword.equals(request.oldPassword())) {
            throw new InvalidPasswordException("The provided old password is incorrect.");
        }

        if (request.newPassword().equals(request.oldPassword())) {
            throw new SameRequestException("The new password cannot be the same as the old password.");
        }

        customer.setPassword(requestedPassword);
        customerRepository.save(customer);

        return new UpdatePasswordResponse("Password updated successfully.");
    }



    @Transactional
    public UpdateAccountStatusResponse updateAccountStatus(Principal principal, UpdateAccountStatusRequest request) {
        Customer customer = findCustomer(principal);

        log.info("Customer '{}' with email '{}' is changing their Account status to '{}'",
                customer.getName(), customer.getEmail(), request.accountStatus());

        AccountStatus currentAccountStatus = customer.getAccountStatus();
        AccountStatus requestedAccountStatus = request.accountStatus();

        if (currentAccountStatus.equals(requestedAccountStatus)) {
            throw new SameRequestException("The requested Account Status is the same as the current one.");
        }

        customer.setAccountStatus(requestedAccountStatus);
        customerRepository.save(customer);

        log.info("Account status successfully updated to '{}'", requestedAccountStatus);

        return new UpdateAccountStatusResponse(
                "Account status updated successfully.",
                requestedAccountStatus
        );
    }

    public CustomerDTO customerInformation(Principal principal){
        Customer customer = findCustomer(principal);
        log.info(customer.getEmail() + " fetching their info");

        List<AddressDTO> addressDTOs = customer.getAddresses()
                .stream()
                .map(address -> new AddressDTO(
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getPostalCode(),
                        address.getAddressType(),
                        address.getDescription()
                ))
                .toList();


        return new CustomerDTO(
                customer.getName(),
                customer.getSurname(),
                customer.getEmail(),
                customer.getMembershipPlan(),
                customer.getTotalOrders(),
                customer.getJoinDate(),
                customer.getBirthdate(),
                addressDTOs
        );
    }

    public List<AddressDTO> getAddresses(Principal p){

        Customer customer = findCustomer(p);
        List<AddressDTO> addresses = customer.getAddresses()
                .stream()
                .map(address -> new AddressDTO(
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getPostalCode(),
                        address.getAddressType(),
                        address.getDescription()
                ))
                .toList();
        return addresses;
    }

    @Transactional
    public AddAddressResponse addAddress(Principal principal, AddAddressRequest request) {
        Customer customer = findCustomer(principal);

        Address newAddress = Address.builder()
                .street(request.addressDTO().street())
                .city(request.addressDTO().city())
                .state(request.addressDTO().state())
                .country(request.addressDTO().country())
                .postalCode(request.addressDTO().postalCode())
                .addressType(request.addressDTO().addressType())
                .description(request.addressDTO().description())
                .customer(customer)
                .build();

        customer.getAddresses().add(newAddress);
        customerRepository.save(customer);


        return new AddAddressResponse(
                "Address added successfully",
                newAddress.getDescription()
        );
    }

    @Transactional
    public String deleteAddress (Principal principal , Integer addressId){
        Customer customer = findCustomer(principal);
        List<Address> addresses = customer.getAddresses();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new AddressNotFoundException("Address not found by id : "+addressId));

        if (!addresses.contains(address)) {
            throw new MissmatchException("None of your addresses id is ."+addressId);
        }
        addresses.remove(address);
        customer.setAddresses(addresses);

        addressRepository.delete(address);

        log.info("Address with id '{}' has been deleted for customer '{}'", addressId, customer.getEmail());
        return "Address with id " + addressId + " deleted successfully.";

    }




}
