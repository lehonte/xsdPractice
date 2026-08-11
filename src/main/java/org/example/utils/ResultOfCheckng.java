package org.example.utils;

import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.example.exceptions.UnknownStatusException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResultOfCheckng {

    @KafkaListener(topics = "resultOfCheking")
    public boolean resultOfChekingActivity(ResultOfChekingEvent event) {
        if (event.status().equals(TransactionStatus.ACCEPTED)) {
            return true;
        } else if (event.status().equals(TransactionStatus.BLOCKED)) {
            return false;
        }
        throw new UnknownStatusException("Неизвестный статус: " + event.status());
    }
}
