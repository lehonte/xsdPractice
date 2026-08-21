package org.example.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtWebSocketInterceptor implements HandshakeInterceptor {

    private final JwtTokenGenerator jwtToken;
    private final JwtUserDetailService jwtUserDetailService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");

        if (protocols == null || protocols.isEmpty()) {
            log.warn("WebSocket отклонен: отсутствует заголовок Sec-WebSocket-Protocol");
            return false;
        }

        for (String protocol : protocols) {
            String hexToken = protocol.trim();
            byte[] bytes = HexFormat.of().parseHex(hexToken);
            String token = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

            String username = jwtToken.getUsername(token);
            try {
                if (username != null) {
                    UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);

                    if (jwtToken.isValidToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken webSocket = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(webSocket);

                        attributes.put("username", username);

                        response.getHeaders().set("Sec-WebSocket-Protocol", hexToken);

                        return true;
                    }
                }
            } catch (IllegalArgumentException e) {
                log.error("Ошибка парсинга Hex-строки: {}", hexToken);
            } catch (Exception e) {
                log.error("Ошибка авторизации JWT в WebSocket", e);
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
