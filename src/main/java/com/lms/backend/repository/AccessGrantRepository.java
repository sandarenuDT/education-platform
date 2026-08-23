package com.lms.backend.repository;

import com.lms.backend.model.AccessGrant;
import com.lms.backend.model.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessGrantRepository extends JpaRepository<AccessGrant, Long> {
    Optional<AccessGrant> findByUserIdAndItemTypeAndItemId(Long userId, ItemType itemType, Long itemId);
}