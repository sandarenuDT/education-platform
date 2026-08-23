package com.lms.backend.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import com.lms.backend.model.enums.ItemType;

/**
 * The single source of truth for "does this user currently have access to
 * this item". Kept separate from Order so manual admin grants/revokes and
 * expiry logic never have to reinterpret raw payment history.
 *
 * @author LMS Backend Team
 */
@Entity
@Table(name = "access_grant")
public class AccessGrant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "order_id")
    private Long orderId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "granted_at", nullable = false)
    private Date grantedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private Date expiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "revoked_at")
    private Date revokedAt;

    public AccessGrant() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.grantedAt == null) {
            this.grantedAt = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Date grantedAt) {
        this.grantedAt = grantedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Date getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Date revokedAt) {
        this.revokedAt = revokedAt;
    }

    public boolean isActive() {
        Date now = new Date();
        boolean notExpired = expiresAt == null || expiresAt.after(now);
        boolean notRevoked = revokedAt == null;
        return notExpired && notRevoked;
    }
}