package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.enums.TransactionStatus;
import org.example.exceptions.AccountNotFoundException;
import org.example.exceptions.TransactionNotFoundException;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateTracsationStatus {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void acceptedTransaction(String transactionNumber) {
        Transaction transaction = transactionRepository.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Транзакция " + transactionNumber + " не найдена"));

        Account account = accountRepository.findByAccountNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + transaction.getAccount().getAccountNumber() + " не найден"));

        account.setBalance(account.getBalance().add(transaction.getAmount()));
        transaction.setStatus(TransactionStatus.ACCEPTED);
        accountRepository.save(account);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void blockedTransaction(String transactionNumber) {
        Transaction transaction = transactionRepository.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new TransactionNotFoundException("Транзакция " + transactionNumber + " не найдена"));

        Account account = accountRepository.findByAccountNumber(transaction.getAccount().getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + transaction.getAccount().getAccountNumber() + " не найден"));

        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        transaction.setStatus(TransactionStatus.BLOCKED);
        accountRepository.save(account);
        transactionRepository.save(transaction);
    }
}
