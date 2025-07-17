package com.xyzbank.digital_onboarding_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.validation.MinAge;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@ToString(exclude = "accounts")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false)
    private String address;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @MinAge(18)
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Country is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();
    
    public Customer(String name, String address, String username, LocalDate dateOfBirth, Country country, String password) {
        this.name = name;
        this.address = address;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.password = password;
        this.accounts = new ArrayList<>();
    }
    
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }
    
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }
}