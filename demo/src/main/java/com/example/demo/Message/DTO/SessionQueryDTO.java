package com.example.demo.Message.DTO;

import lombok.Data;

@Data
public class SessionQueryDTO {
    private Integer userId;
    private Integer counselorId;
    private String status; // "IN_PROGRESS", "COMPLETED"
}