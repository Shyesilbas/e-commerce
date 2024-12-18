package com.serhat.customer.repository;

import com.serhat.customer.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Optional<Admin> findByPhone(String phone);

    Optional<Admin> findByEmail(String email);
}
