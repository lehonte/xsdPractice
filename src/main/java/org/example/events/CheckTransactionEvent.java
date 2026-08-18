package org.example.events;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CheckTransactionEvent(String phoneNumber, String transactionNumber, BigDecimal amount, String owner) {
}
