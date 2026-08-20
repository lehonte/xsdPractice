package org.example.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HexFormat;

@Slf4j
@RequiredArgsConstructor
public class JwtWebSocketFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtToken;
    private final JwtUserDetailService jwtUserDetailService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // фильтр нужен только для webSocket
        return !request.getRequestURI().startsWith("/xsdPractice");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String hexToken = request.getHeader("Sec-WebSocket-Protocol");
        byte[] bytes = HexFormat.of().parseHex(hexToken);
        String token = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

        try {
            String username = jwtToken.getUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);

                if (jwtToken.isValidToken(token, userDetails)) {
                    Authentication userAuth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(userAuth);
                }
            }
        } catch (Exception e) {
            log.error("JWT ошибка", e);
        }
        filterChain.doFilter(request, response);
    }
}