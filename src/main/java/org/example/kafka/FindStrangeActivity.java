package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.StrangeTransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindStrangeActivity {

    private final KafkaTemplate<String, StrangeTransactionEvent> kafkaTemplate;

    public boolean findStrangeActivity(BigDecimal amount, String phoneNumber, String transactionNumber) {
        if (amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            log.info("Найдена странная активность: сумма перевода {} больше 5000", amount);
            StrangeTransactionEvent event = new StrangeTransactionEvent(phoneNumber, transactionNumber);
            kafkaTemplate.send("strange_transaction_topic", event);
            log.info("Транзакция {} отправлена на проверку", transactionNumber);
            return true;
        }
        return false;
    }
}
