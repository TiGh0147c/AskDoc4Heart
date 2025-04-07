package com.example.demo.Queue.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePositionDTO {
    private Integer queueId;
    private Integer currentNumber;
    private Integer aheadCount;
    private Integer estimatedMinutes;
    private String statusMessage;

    public QueuePositionDTO(String errorMessage) {
        this.statusMessage = errorMessage;
    }
}