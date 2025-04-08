package com.example.demo.Message.DTO;

import lombok.Data;

@Data
public class SessionVO {
    private Integer sessionId;
    private String startTime;
    private String endTime;
    private String status;
    private Integer counselorId;
    private Integer userId;
    private Boolean userHasEvaluated;
    private Boolean counselorHasEvaluated;
}