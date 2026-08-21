package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.CheckTransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindActivity {

    private final KafkaTemplate<String, CheckTransactionEvent> kafkaTemplate;

    public void findActivity(BigDecimal amount, String email, String transactionNumber, String owner) {
        CheckTransactionEvent event = CheckTransactionEvent.newBuilder()
                .setOwner(owner)
                .setAmount(amount)
                .setEmail(email)
                .setTransactionNumber(transactionNumber)
                .build();
        kafkaTemplate.send("check_transaction_topic", event);
        log.info("Транзакция {} отправлена на проверку", transactionNumber);
    }
}
