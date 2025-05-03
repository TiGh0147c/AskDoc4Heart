package com.example.demo.counseling.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Component
public class WebSocketExceptionHandler implements WebSocketHandlerDecoratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketExceptionHandler.class);

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                logger.error("WebSocket transport error", exception);
                session.close(CloseStatus.SERVER_ERROR.withReason("Internal server error"));
            }
        };
    }
}