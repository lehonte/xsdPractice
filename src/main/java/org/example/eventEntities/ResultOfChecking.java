package org.example.eventEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "results_of_checking")
@Getter
@Setter
public class ResultOfChecking {

    @Id
    @Column(name = "transaction_number")
    private String transactionNumber;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public ResultOfChecking(String transactionNumber) {
        this.transactionNumber = transactionNumber;
        this.processedAt = LocalDateTime.now();
    }
}
