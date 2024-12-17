package com.serhat.customer.repository;

import com.serhat.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);
}
