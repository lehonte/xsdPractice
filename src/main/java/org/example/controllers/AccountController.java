package org.example.controllers;

import com.example.demo.generated.*;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import org.example.services.AccountService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ObjectFactory objectFactory = new ObjectFactory();

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new org.springframework.security.access.AccessDeniedException("Пользователь не авторизован");
        }
        return userDetails.getUsername();
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "GetAccountBalanceRequest")
    @ResponsePayload
    public GetAccountBalanceResponse getAccountBalance(@RequestPayload final GetAccountBalanceRequest request) {
        String username = getUsername();
        return accountService.getAccountBalance(request, username);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "DepositRequest")
    @ResponsePayload
    public JAXBElement<OperationResponseType> deposit(@RequestPayload final JAXBElement<OperationRequestType> requestXML) {
        OperationRequestType request = requestXML.getValue();

        String username = getUsername();
        OperationResponseType response = accountService.deposit(request, username);
        return objectFactory.createDepositResponse(response);
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "WithdrawRequest")
    @ResponsePayload
    public JAXBElement<OperationResponseType> withdraw(@RequestPayload final JAXBElement<OperationRequestType> requestXML) {
        OperationRequestType request = requestXML.getValue();

        String username = getUsername();
        OperationResponseType response = accountService.withdraw(request, username);
        return objectFactory.createWithdrawResponse(response);
    }
}
