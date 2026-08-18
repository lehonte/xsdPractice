package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controllers.WebSocketHandler;
import org.example.entities.Transaction;
import org.example.enums.TransactionStatus;
import org.example.eventEntities.ResultOfChecking;
import org.example.events.ResultOfChekingEvent;
import org.example.exceptions.TransactionNotFoundException;
import org.example.exceptions.UnknownStatusException;
import org.example.repositories.ResultOfCheckingRepository;
import org.example.repositories.TransactionRepository;
import org.example.utils.UpdateTracsationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendResultOfCheckng {

    private final UpdateTracsationStatus updateTracsationStatus;
    private final WebSocketHandler webSocketHandler;
    private final ResultOfCheckingRepository resultOfCheckingRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @KafkaListener(topics = "result_of_checking")
    public void resultOfChekingActivity(ResultOfChekingEvent event) {

        if (resultOfCheckingRepository.existsByTransactionNumber(event.transactionNumber())) {
            log.info("Результат транзакции {} уже отправлен на обработку", event.transactionNumber());
            return;
        }

        Transaction transaction = transactionRepository.findByTransactionNumber(event.transactionNumber())
                .orElseThrow(() -> new TransactionNotFoundException(event.transactionNumber()));

        resultOfCheckingRepository.save(new ResultOfChecking(event.transactionNumber()));

        log.info("Обработанная транзация {} получена с обработки", event.transactionNumber());

        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                             <transactionResult xmlns="http://example.org/xsdPractice">
                                 <account>%s</account>
                                 <type>%s</type>
                                 <amount>%s</amount>
                                 <status>%s</status>
                                 <date>%s</date>
                             </transactionResult>
                """.formatted(transaction.getAccount(),
                transaction.getType(),
                transaction.getAmount(),
                event.status(),
                transaction.getDate());

        switch (event.status()) {
            case ACCEPTED: {
                updateTracsationStatus.acceptedTransaction(event.transactionNumber());
                break;
            }
            case BLOCKED: {
                updateTracsationStatus.blockedTransaction(event.transactionNumber());
                break;
            }
            default: {
                throw new UnknownStatusException("Неизвестный статус: " + event.status());
            }
        }

        //нельзя отсюда websocket прямо вызывать тк мгновенно улетит соо и это не откатится если в бд не смог обновиться
        //поэтому юзаем TransactionSynchronizationAdapter, чтобы отправлялось соо только после успешного коммита в бд
        //закомитится - значит отката уже сто проц не будет
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                webSocketHandler.sending(xml);
                log.info("Сообщение со статусом транзакции {} перенаправлено клиенту", event.transactionNumber());
            }
        });
    }
}
