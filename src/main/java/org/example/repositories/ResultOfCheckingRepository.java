package org.example.repositories;

import org.example.eventEntities.ResultOfChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultOfCheckingRepository extends JpaRepository<ResultOfChecking, String> {
    Boolean existsByTransactionNumber(String transactionNumber);
}
