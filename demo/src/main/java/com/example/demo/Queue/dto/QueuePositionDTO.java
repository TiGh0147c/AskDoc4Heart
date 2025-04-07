package com.example.demo.Queue.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


//用于封装用户在队列中的位置信息的数据传输对象

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePositionDTO {
    private Integer queueId;
    private Integer currentNumber;//当前处理的队列编号，表示当前正在被处理的用户在队列中的位置编号。
    private Integer aheadCount;//在当前用户之前的排队人数，表示还有多少人排在当前用户前面。
    private Integer estimatedMinutes;//预计等待时间（分钟），根据当前队列的速度估算出的大致等待时间。
    private String statusMessage;//状态消息，用于存储额外的状态信息或错误消息。，存储错误信息使用
    public QueuePositionDTO(String errorMessage) {
        this.statusMessage = errorMessage;
    }
}