package org.example.config;

import org.example.controllers.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.codec.binary.Hex.decodeHex;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/xsdPractice")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    private String selectProtocol(List<String> requestedProtocols, WebSocketHandler wsHandler) {
                        if (!requestedProtocols.isEmpty()) {
                            String hexToken = requestedProtocols.get(0);

                            try {
                                String rawJwt = Arrays.toString(decodeHex(hexToken));
                                return hexToken;
                            } catch (Exception e) {
                                return null;
                            }
                        }
                        return null;
                    }
                })
                .setAllowedOrigins("*");
    }
}
