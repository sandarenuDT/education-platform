
package com.lms.backend.service;

import com.lms.backend.model.AccessGrant;
import com.lms.backend.model.User;
import com.lms.backend.model.enums.ItemType;
import com.lms.backend.repository.AccessGrantRepository;
import com.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * THE single source of truth for "does this user currently have access to
 * this item". Every protected endpoint (book download, recording stream URL)
 * must call hasAccess() here — never re-implement this check inline in a
 * controller or another service.
 */
@Service
public class AccessControlService {

    private final AccessGrantRepository accessGrantRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccessControlService(AccessGrantRepository accessGrantRepository, UserRepository userRepository) {
        this.accessGrantRepository = accessGrantRepository;
        this.userRepository = userRepository;
    }

    public boolean hasAccess(Long userId, ItemType itemType, Long itemId) {
        Optional<AccessGrant> grant = accessGrantRepository.findByUserIdAndItemTypeAndItemId(userId, itemType, itemId);
        return grant.isPresent() && grant.get().isActive();
    }

    // Called by OrderService once a payment is confirmed (currently: always,
    // since real payment integration is Step 9).
    public AccessGrant grantAccess(Long userId, ItemType itemType, Long itemId, Long orderId) {
        // Idempotent: if a grant already exists (e.g. re-purchase or retried
        // request), reactivate it instead of creating a duplicate row.
        AccessGrant grant = accessGrantRepository
                .findByUserIdAndItemTypeAndItemId(userId, itemType, itemId)
                .orElseGet(AccessGrant::new);

        if (grant.getId() == null) {
            // getReferenceById gives a lazy proxy carrying only the ID — sets
            // the FK correctly on save WITHOUT loading (or worse, trying to
            // re-insert) the full User row.
            User userRef = userRepository.getReferenceById(userId);
            grant.setUser(userRef);
            grant.setItemType(itemType);
            grant.setItemId(itemId);
        }

        grant.setOrderId(orderId);
        grant.setRevokedAt(null); // clears any prior revocation
        return accessGrantRepository.save(grant);
    }
}