package org.example.config;

import org.example.controllers.WebSocketHandler;
import org.example.jwt.JwtWebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;
    private final JwtWebSocketInterceptor jwtInterceptor;

    WebSocketConfig(WebSocketHandler webSocketHandler, JwtWebSocketInterceptor jwtInterceptor) {
        this.webSocketHandler = webSocketHandler;
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/xsdPractice")
                .addInterceptors(jwtInterceptor)
                .setAllowedOrigins("*");
    }
}
