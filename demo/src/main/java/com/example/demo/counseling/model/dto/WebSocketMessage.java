package com.example.demo.counseling.model.dto;

import com.example.demo.counseling.model.enums.MessageType;
import com.example.demo.counseling.model.enums.SenderRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private MessageType type;
    private String content;
    private SenderRole senderRole;
    private Long senderId;
    private LocalDateTime timestamp;
}