package org.example.repositories;

import org.example.entities.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = "account")
    List<Transaction> findByAccount_AccountNumber(BigInteger accountNumber);

    @EntityGraph(attributePaths = "account")
    Optional<Transaction> findByTransactionNumber(String transactionNumber);
}
