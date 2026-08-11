package org.example.services;

import com.example.demo.generated.GetAccountBalanceRequest;
import com.example.demo.generated.GetAccountBalanceResponse;
import com.example.demo.generated.OperationRequestType;
import com.example.demo.generated.OperationResponseType;
import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.events.StrangeTransactionEvent;
import org.example.exceptions.InsufficientFundException;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.example.exceptions.AccountNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, StrangeTransactionEvent> kafkaTemplate;

    public GetAccountBalanceResponse getAccountBalance(GetAccountBalanceRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        GetAccountBalanceResponse response = new GetAccountBalanceResponse();
        response.setOwnerName(account.getOwner());
        response.setBalance(account.getBalance());

        return response;
    }

    public OperationResponseType deposit(OperationRequestType request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        findStrangeActivity(request, account);

        account.setBalance(account.getBalance().add(request.getAmount()));

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);
        transaction.setType("deposit");
        transaction.setDate(LocalDate.now());
        transactionRepository.save(transaction);

        OperationResponseType response = new OperationResponseType();
        response.setBalance(account.getBalance());

        return response;
    }

    public OperationResponseType withdraw(OperationRequestType request) throws InsufficientFundException {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        findStrangeActivity(request, account);

        OperationResponseType response = new OperationResponseType();

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundException("Вы не можете снять больше денег, чем есть у вас на счете");
        } else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            response.setBalance(account.getBalance());
            accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAmount(request.getAmount());
            transaction.setAccount(account);
            transaction.setType("withdraw");
            transaction.setDate(LocalDate.now());
            transactionRepository.save(transaction);
        }

        return response;
    }

    private void findStrangeActivity(OperationRequestType request, Account account) {
        if (request.getAmount().compareTo(BigDecimal.valueOf(5000)) < 0) {
            StrangeTransactionEvent event = new StrangeTransactionEvent(account.getPhoneNumber());
            kafkaTemplate.send("", event);
        }
    }
}
