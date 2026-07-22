package org.example.controllers;

import com.example.demo.generated.GetAccountBalanceRequest;
import com.example.demo.generated.GetAccountBalanceResponse;
import com.example.demo.generated.OperationRequestType;
import com.example.demo.generated.OperationResponseType;
import lombok.RequiredArgsConstructor;
import org.example.services.AccountService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "GetAccountBalanceRequest")
    @ResponsePayload
    public GetAccountBalanceResponse getAccountBalance(@RequestPayload final GetAccountBalanceRequest request) {
        return accountService.getAccountBalance(request);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "DepositRequest")
    @ResponsePayload
    public OperationResponseType deposit(@RequestPayload final OperationRequestType operationRequestType) {
        return accountService.deposit(operationRequestType);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "WithdrawRequest")
    @ResponsePayload
    public OperationResponseType withdraw(@RequestPayload final OperationRequestType operationRequestType) {
        return accountService.withdraw(operationRequestType);
    }
}
