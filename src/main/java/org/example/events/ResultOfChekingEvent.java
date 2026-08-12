package org.example.events;

import org.example.enums.TransactionStatus;

public record ResultOfChekingEvent(TransactionStatus status, String transactionNumber) {
}
