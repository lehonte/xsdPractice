package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.example.events.StrangeTransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class FindStrangeActivity {

    private final KafkaTemplate<String, StrangeTransactionEvent> kafkaTemplate;

    public void findStrangeActivity(BigDecimal amount, String phoneNumber, String transactionNumber) {
        if (amount.compareTo(BigDecimal.valueOf(5000)) < 0) {
            StrangeTransactionEvent event = new StrangeTransactionEvent(phoneNumber, transactionNumber);
            kafkaTemplate.send("strange_transaction_topic", event);
        }
    }
}
