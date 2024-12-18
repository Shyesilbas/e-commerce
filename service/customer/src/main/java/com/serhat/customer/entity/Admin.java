package com.serhat.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "surname",nullable = false)
    private String surname;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birthdate",nullable = false)
    private LocalDate birthdate;

    @Email
    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "phone",nullable = false,unique = true)
    private String phone;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "join_date",nullable = false)
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;
}
