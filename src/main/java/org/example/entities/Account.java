package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner", unique = true, nullable = false)
    private String owner;

    @Column(name = "account_number", unique = true, nullable = false)
    private BigInteger accountNumber;

    @Column(name = "balance")
    private BigDecimal balance;
}
