package com.example.demo.Queue.service;

/**
 * QueueService 接口定义了与队列管理相关的业务逻辑方法。
 */

import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import java.util.List;

public interface QueueService {
    // 用户加入队列
    QueueDTO joinQueue(QueueDTO queueDTO);
    //获取指定队列ID的用户在队列中的位置信息。
    QueuePositionDTO getQueuePosition(Integer queueId);
    //根据用户ID获取该用户的队列条目
    List<QueueDTO> getQueuesByUserId(Integer userId);
    //获取指定咨询师当前正在处理的所有队列条目。
    List<QueueDTO> getCurrentQueue(Integer counselorId);
    //更新指定队列条目的状态。
    boolean updateQueueStatus(Integer queueId, String status);
    //获取所有处于活动状态的队列条目。
    List<QueueDTO> getActiveQueues();
    //获取
    ///**
    //     * 调用下一个待处理的队列条目（通常由咨询师使用）。
    //     *
    //     * @param counselorId 咨询师ID
    //     * @return 返回下一个待处理的 QueueDTO 对象
    //     */
    QueueDTO callNextPatient(Integer counselorId);

    void clearAllQueues(); // 新增方法
}