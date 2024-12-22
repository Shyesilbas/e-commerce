package com.serhat.order.repository;

import com.serhat.order.entity.Order;
import com.serhat.order.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByCustomerId(Integer customerId);

    List<Order> findByCustomerIdAndOrderDateBetweenAndStatus(Integer integer, LocalDateTime from, LocalDateTime to, Status status);
}
