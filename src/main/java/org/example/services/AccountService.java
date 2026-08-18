package org.example.services;

import com.example.demo.generated.GetAccountBalanceRequest;
import com.example.demo.generated.GetAccountBalanceResponse;
import com.example.demo.generated.OperationRequestType;
import com.example.demo.generated.OperationResponseType;
import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.enums.TransactionStatus;
import org.example.exceptions.InsufficientFundException;
import org.example.kafka.FindStrangeActivity;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.example.utils.GenerateTrasaction;
import org.springframework.stereotype.Service;
import org.example.exceptions.AccountNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FindStrangeActivity findStrangeActivity;
    private final GenerateTrasaction generateTrasaction;

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

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);
        transaction.setType("deposit");
        transaction.setDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.PENDING);
        String transactionNumber = generateTrasaction.generateTransactionNumber(transaction);
        transaction.setTransactionNumber(transactionNumber);
        transactionRepository.save(transaction);

        findStrangeActivity.findStrangeActivity(request.getAmount(), account.getPhoneNumber(), transactionNumber);

        OperationResponseType response = new OperationResponseType();

        if (findStrangeActivity.findStrangeActivity(request.getAmount(), account.getPhoneNumber(), transactionNumber)) {
            response.setAccountNumber(account.getAccountNumber());
            response.setAmount(request.getAmount());
            response.setType("deposit");
            response.setStatus(String.valueOf(TransactionStatus.PENDING));
            return response;
        }

        account.setBalance(account.getBalance().add(transaction.getAmount()));
        transaction.setStatus(TransactionStatus.ACCEPTED);
        accountRepository.save(account);
        transactionRepository.save(transaction);

        response.setAccountNumber(account.getAccountNumber());
        response.setAmount(request.getAmount());
        response.setType("deposit");
        response.setStatus(String.valueOf(TransactionStatus.ACCEPTED));

        return response;
    }

    public OperationResponseType withdraw(OperationRequestType request) throws InsufficientFundException {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        OperationResponseType response = new OperationResponseType();

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundException("Вы не можете снять больше денег, чем есть у вас на счете");
        } else {

            Transaction transaction = new Transaction();
            transaction.setAmount(request.getAmount());
            transaction.setAccount(account);
            transaction.setType("withdraw");
            transaction.setDate(LocalDate.now());
            transaction.setStatus(TransactionStatus.PENDING);
            String transactionNumber = generateTrasaction.generateTransactionNumber(transaction);
            transaction.setTransactionNumber(transactionNumber);
            transactionRepository.save(transaction);

            if (findStrangeActivity.findStrangeActivity(request.getAmount(), account.getPhoneNumber(), transactionNumber)) {
                response.setAccountNumber(account.getAccountNumber());
                response.setAmount(request.getAmount());
                response.setType("withdraw");
                response.setStatus(String.valueOf(TransactionStatus.PENDING));
                return response;
            }

            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            transaction.setStatus(TransactionStatus.ACCEPTED);
            accountRepository.save(account);
            transactionRepository.save(transaction);

            response.setAccountNumber(account.getAccountNumber());
            response.setAmount(request.getAmount());
            response.setType("withdraw");
            response.setStatus(String.valueOf(TransactionStatus.ACCEPTED));
        }

        return response;
    }
}
