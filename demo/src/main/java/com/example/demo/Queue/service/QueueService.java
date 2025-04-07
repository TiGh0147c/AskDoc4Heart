package com.example.demo.Queue.service;

import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import java.util.List;

public interface QueueService {
    QueueDTO joinQueue(QueueDTO queueDTO) throws IllegalStateException;
    QueuePositionDTO getQueuePosition(Integer queueId);
    List<QueueDTO> getQueuesByUserId(Integer userId);
    List<QueueDTO> getCurrentQueue(Integer counselorId);
    boolean updateQueueStatus(Integer queueId, String status);
    List<QueueDTO> getActiveQueues();
    QueueDTO callNextPatient(Integer counselorId) throws IllegalStateException;
    boolean cancelQueue(Integer queueId);
    int getWaitingCount(Integer counselorId);
    int getInProgressCount(Integer counselorId);
}