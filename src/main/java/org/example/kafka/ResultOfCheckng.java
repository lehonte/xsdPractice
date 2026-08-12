package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.example.exceptions.UnknownStatusException;
import org.example.utils.UpdateTracsationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResultOfCheckng {

    private final UpdateTracsationStatus updateTracsationStatus;

    @KafkaListener(topics = "result_of_checking")
    public boolean resultOfChekingActivity(ResultOfChekingEvent event) {
        if (event.status().equals(TransactionStatus.ACCEPTED)) {
            updateTracsationStatus.acceptedTransaction(event.transactionNumber());
        } else if (event.status().equals(TransactionStatus.BLOCKED)) {
            updateTracsationStatus.blockedTransaction(event.transactionNumber());
        }
        throw new UnknownStatusException("Неизвестный статус: " + event.status());
    }
}
