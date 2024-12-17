package com.serhat.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serhat.customer.dto.object.AddressDTO;
import com.serhat.customer.entity.Address;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record CreateCustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Surname is required")
        @Size(min = 2, max = 100, message = "Surname must be between 2 and 100 characters")
        String surname,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull(message = "Birthdate is required")
        LocalDate birthdate,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$", message = "Phone number must match the pattern xxxx xxx xx xx")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,


        @NotNull(message = "Address is Required!")
        List<AddressDTO> addresses
) {
}
