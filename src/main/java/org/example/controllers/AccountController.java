package org.example.controllers;

import com.example.demo.generated.*;
import jakarta.xml.bind.JAXBElement;
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
    private final ObjectFactory objectFactory = new ObjectFactory();

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "GetAccountBalanceRequest")
    @ResponsePayload
    public GetAccountBalanceResponse getAccountBalance(@RequestPayload final GetAccountBalanceRequest request) {
        return accountService.getAccountBalance(request);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "DepositRequest")
    @ResponsePayload
    public JAXBElement<OperationResponseType> deposit(@RequestPayload final JAXBElement<OperationRequestType> requestXML) {
        OperationRequestType request = requestXML.getValue();
        OperationResponseType response = accountService.deposit(request);
        return objectFactory.createDepositResponse(response);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "WithdrawRequest")
    @ResponsePayload
    public JAXBElement<OperationResponseType> withdraw(@RequestPayload final JAXBElement<OperationRequestType> requestXML) {
        OperationRequestType request = requestXML.getValue();
        OperationResponseType response = accountService.withdraw(request);
        return objectFactory.createWithdrawResponse(response);
    }
}
