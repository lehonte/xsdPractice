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
import org.example.kafka.FindActivity;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.example.utils.GenerateTrasaction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.example.exceptions.AccountNotFoundException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FindActivity findActivity;
    private final GenerateTrasaction generateTrasaction;

    public GetAccountBalanceResponse getAccountBalance(GetAccountBalanceRequest request, String username) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        if (!account.getOwner().equals(username)) throw new AccessDeniedException("Вы не имеете доступа к счету "
                + request.getAccountNumber());

        GetAccountBalanceResponse response = new GetAccountBalanceResponse();
        response.setOwnerName(account.getOwner());
        response.setBalance(account.getBalance());

        return response;
    }

    public OperationResponseType deposit(OperationRequestType request, String username) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        if (!account.getOwner().equals(username)) throw new AccessDeniedException("Вы не имеете доступа к счету "
                + request.getAccountNumber());

        OperationResponseType response = new OperationResponseType();

        makeTransaction(request, account, "deposit");

        response.setAccountNumber(account.getAccountNumber());
        response.setAmount(request.getAmount());
        response.setType("deposit");
        response.setStatus(String.valueOf(TransactionStatus.PENDING));
        return response;
    }

    public OperationResponseType withdraw(OperationRequestType request, String username) throws InsufficientFundException {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        if (!account.getOwner().equals(username)) throw new AccessDeniedException("Вы не имеете доступа к счету "
                + request.getAccountNumber());

        OperationResponseType response = new OperationResponseType();

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundException("Вы не можете снять больше денег, чем есть у вас на счете");
        } else {

            makeTransaction(request, account, "withdraw");

            response.setAccountNumber(account.getAccountNumber());
            response.setAmount(request.getAmount());
            response.setType("withdraw");
            response.setStatus(String.valueOf(TransactionStatus.PENDING));
            return response;
        }
    }

    private void makeTransaction(OperationRequestType request, Account account, String operationType) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);
        transaction.setType(operationType);
        transaction.setDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.PENDING);
        String transactionNumber = generateTrasaction.generateTransactionNumber(transaction);
        transaction.setTransactionNumber(transactionNumber);
        transactionRepository.save(transaction);

        findActivity.findActivity(request.getAmount(), account.getEmail(), transactionNumber, account.getOwner());
    }
}
