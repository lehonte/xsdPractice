package org.example.services;

import com.example.demo.generated.GetTransactionHistoryRequest;
import com.example.demo.generated.GetTransactionHistoryResponse;
import com.example.demo.generated.TransactionStatusEnum;
import com.example.demo.generated.TransactionType;
import lombok.RequiredArgsConstructor;
import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.enums.TransactionStatus;
import org.example.exceptions.AccountNotFoundException;
import org.example.repositories.AccountRepository;
import org.example.repositories.TransactionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public GetTransactionHistoryResponse getTransactionHistory(GetTransactionHistoryRequest request, String username) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Счет " + request.getAccountNumber() + " не найден"));

        if (!account.getOwner().equals(username)) throw new AccessDeniedException("Вы не имеете доступа к счету "
                + request.getAccountNumber());

        List<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(request.getAccountNumber());

        GetTransactionHistoryResponse response = new GetTransactionHistoryResponse();

        if (transactions.isEmpty()) return response;

        DatatypeFactory factory;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        response.getTransaction().addAll(transactions.stream().map(transaction -> {
            XMLGregorianCalendar xmlCalendar = factory.newXMLGregorianCalendar(transaction.getDate().toString());

            TransactionType transactionType = new TransactionType();
            transactionType.setAmount(transaction.getAmount());
            transactionType.setType(transaction.getType());
            transactionType.setDate(xmlCalendar);
            transactionType.setStatus(statusToResponse(transaction.getStatus()));
            return transactionType;
        }).toList());

        return response;
    }


    private TransactionStatusEnum statusToResponse(TransactionStatus status) {
        return switch (status) {
            case ACCEPTED -> TransactionStatusEnum.ACCEPTED;
            case BLOCKED -> TransactionStatusEnum.BLOCKED;
            case PENDING -> TransactionStatusEnum.PENDING;
        };
    }
}
