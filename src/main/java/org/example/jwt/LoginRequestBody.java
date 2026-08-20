package org.example.jwt;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginRequestBody extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public LoginRequestBody(HttpServletRequest request) throws IOException {
        super(request);
        this.requestBody = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(requestBody);
    }

    @Override
    public BufferedReader getReader()  {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestBody)));
    }
}
