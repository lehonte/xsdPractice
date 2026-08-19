package org.example.controllers;

import com.example.demo.generated.LoginRequest;
import com.example.demo.generated.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.example.services.UserService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PayloadRoot(namespace = "http://example.org/xsdPractice", localPart = "LoginRequest")
    @ResponsePayload
    public LoginResponse login(@RequestPayload final LoginRequest request) {
        return userService.login(request);
    }
}
