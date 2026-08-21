package org.example.services;

import com.example.demo.generated.GetTransactionHistoryRequest;
import com.example.demo.generated.GetTransactionHistoryResponse;
import com.example.demo.generated.TransactionStatusEnum;
import com.example.demo.generated.TransactionType;
import lombok.RequiredArgsConstructor;
import org.example.entities.Transaction;
import org.example.enums.TransactionStatus;
import org.example.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public GetTransactionHistoryResponse getTransactionHistory(GetTransactionHistoryRequest request) {
        List<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(request.getAccountNumber());

        GetTransactionHistoryResponse response = new GetTransactionHistoryResponse();

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
