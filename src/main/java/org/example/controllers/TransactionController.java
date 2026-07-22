package org.example.controllers;

import com.example.demo.generated.GetTransactionHistoryRequest;
import com.example.demo.generated.GetTransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.example.services.TransactionService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "GetTransactionHistoryRequest")
    @ResponsePayload
    public GetTransactionHistoryResponse getTransactionHistory(@RequestPayload final GetTransactionHistoryRequest request) {
        return transactionService.getTransactionHistory(request);
    }
}
