package com.example.demo.counseling.model.dto;

import lombok.Data;

@Data
public class SendMessageDTO {
    private Long userId;
    private String content;
}
