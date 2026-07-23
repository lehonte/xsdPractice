package org.example.services;

import com.example.demo.generated.GetAccountBalanceRequest;
import com.example.demo.generated.GetAccountBalanceResponse;
import com.example.demo.generated.OperationRequestType;
import com.example.demo.generated.OperationResponseType;
import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.exceptions.InsufficientFundException;
import org.example.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.example.exceptions.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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

        account.setBalance(account.getBalance().add(request.getAmount()));

        accountRepository.save(account);

        OperationResponseType response = new OperationResponseType();
        response.setBalance(account.getBalance());

        return response;
    }

    public OperationResponseType withdraw(OperationRequestType request) throws InsufficientFundException {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        OperationResponseType response = new OperationResponseType();

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundException("Вы не можете снять больше денег, чем есть у вас на счете");
        } else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            response.setBalance(account.getBalance());
            accountRepository.save(account);
        }

        return response;
    }
}
