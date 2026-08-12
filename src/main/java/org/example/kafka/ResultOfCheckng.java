package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controllers.WebSocketHandler;
import org.example.enums.TransactionStatus;
import org.example.events.ResultOfChekingEvent;
import org.example.exceptions.UnknownStatusException;
import org.example.utils.UpdateTracsationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultOfCheckng {

    private final UpdateTracsationStatus updateTracsationStatus;
    private final WebSocketHandler webSocketHandler;

    @KafkaListener(topics = "result_of_checking")
    public void resultOfChekingActivity(ResultOfChekingEvent event) {

        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                             <transactionResult xmlns="http://example.org/xsdPractice">
                                 <number>%s</number>
                                 <status>%s</status>
                             </transactionResult>
                """.formatted(event.transactionNumber(), event.status());

        if (event.status().equals(TransactionStatus.ACCEPTED)) {
            updateTracsationStatus.acceptedTransaction(event.transactionNumber());
            webSocketHandler.sending(xml);

        } else if (event.status().equals(TransactionStatus.BLOCKED)) {
            updateTracsationStatus.blockedTransaction(event.transactionNumber());
            webSocketHandler.sending(xml);
        } else {
            throw new UnknownStatusException("Неизвестный статус: " + event.status());
        }
    }
}
