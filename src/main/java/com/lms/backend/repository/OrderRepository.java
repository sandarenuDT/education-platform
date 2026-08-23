package com.lms.backend.repository;

import com.lms.backend.model.Order;
import com.lms.backend.model.enums.ItemType;
import com.lms.backend.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndItemTypeAndItemIdAndStatus(Long userId, ItemType itemType, Long itemId, OrderStatus status);
}