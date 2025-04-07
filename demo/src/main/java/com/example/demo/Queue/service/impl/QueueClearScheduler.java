package com.example.demo.Queue.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QueueClearScheduler {

    @Autowired
    private QueueServiceImpl queueService;

    /**
     * 每天下午4:40执行，清空所有队列。
     */
    @Scheduled(cron = "0 40 16 * * ?")
    public void clearQueues() {
        // 清空所有队列条目
        queueService.clearAllQueues(); //
    }
}