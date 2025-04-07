package com.example.demo.Queue.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueDTO {
    private Integer queueId;
    private Integer queueNumber;
    private String userNumber;
    private String joinQueueTime;
    private String exitQueueTime;
    private String queueStatus;
    private Integer userId;
    private Integer counselorId;
    private String statusMessage;

    public QueueDTO(String errorMessage) {
        this.statusMessage = errorMessage;
    }
}