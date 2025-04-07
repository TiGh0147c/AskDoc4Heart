package com.example.demo.Queue.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Queue {
    private Integer queueId;
    private Integer queueNumber;
    private String userNumber;
    private LocalDateTime joinQueueTime;
    private LocalDateTime exitQueueTime;
    private String queueStatus; // waiting/in_progress/completed/cancelled
    private Integer userId;
    private Integer counselorId;
}