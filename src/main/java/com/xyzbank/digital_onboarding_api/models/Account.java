package com.xyzbank.digital_onboarding_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.xyzbank.digital_onboarding_api.enums.AccountType;
import com.xyzbank.digital_onboarding_api.enums.Currency;
import com.xyzbank.digital_onboarding_api.validation.ValidIban;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@ToString(exclude = "customer")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "IBAN is required")
    @ValidIban
    @Column(nullable = false, unique = true)
    private String iban;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    
    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    public Account(String iban, BigDecimal balance, AccountType accountType, Currency currency, Customer customer) {
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.currency = currency;
        this.customer = customer;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}