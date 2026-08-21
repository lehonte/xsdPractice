package org.example.services;

import com.example.demo.generated.LoginRequest;
import com.example.demo.generated.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwt.JwtTokenGenerator;
import org.example.jwt.JwtUserDetailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtUserDetailService jwtUserDetailService;

    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(request.getUsername());

        response.setToken(jwtTokenGenerator.generateToken(userDetails));
        response.setUsername(request.getUsername());
        response.setStatus("Успешный вход в систему");
        return response;
    }
}
