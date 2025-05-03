package com.example.demo.counseling.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.example.demo.counseling.handler.ConsultationHandler;
import com.example.demo.counseling.interceptor.AuthHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ConsultationHandler consultationHandler;

    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;

    @Autowired
    private WebSocketExceptionHandler exceptionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                        exceptionHandler.decorate(consultationHandler),
                        "/ws/consultation/{sessionId}")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}