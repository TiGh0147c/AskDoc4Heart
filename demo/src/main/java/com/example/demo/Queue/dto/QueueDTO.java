package com.example.demo.Queue.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueDTO {
    private Integer queueId;
    private Integer queueNumber;//队列编号
    private String userNumber;//用户再队列中的位置
    private String joinQueueTime;//加入队列时间
    private String exitQueueTime;
    private String queueStatus;//在队列中的状态，例如，已取消，等待中，已结束
    private Integer userId;
    private Integer counselorId;
    private String statusMessage;//没有作用，用于测试这个功能时使用的错误码

    public QueueDTO(String errorMessage) {
        this.statusMessage = errorMessage;
    }
}