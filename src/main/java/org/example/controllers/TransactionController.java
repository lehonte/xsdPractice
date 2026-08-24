package org.example.controllers;

import com.example.demo.generated.GetTransactionHistoryRequest;
import com.example.demo.generated.GetTransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.example.services.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new org.springframework.security.access.AccessDeniedException("Пользователь не авторизован");
        }
        return userDetails.getUsername();
    }

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "GetTransactionHistoryRequest")
    @ResponsePayload
    public GetTransactionHistoryResponse getTransactionHistory(@RequestPayload final GetTransactionHistoryRequest request) {
        String username = getUsername();
        return transactionService.getTransactionHistory(request, username);
    }
}
