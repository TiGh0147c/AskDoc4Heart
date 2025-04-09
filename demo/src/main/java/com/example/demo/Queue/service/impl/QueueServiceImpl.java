package com.example.demo.Queue.service.impl;

import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import com.example.demo.Queue.entity.Queue;
import com.example.demo.Queue.mapper.QueueMapper;
import com.example.demo.Queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class QueueServiceImpl implements QueueService {
    private static final int MAX_WAITING = 10;
    private static final int MAX_IN_PROGRESS = 3;
    private static final LocalTime START_TIME = LocalTime.of(9, 0); // 早上9点
    private static final LocalTime END_TIME = LocalTime.of(16, 40); // 下午4:40
    @Autowired
    private QueueMapper queueMapper;

    @Override
    @Transactional
    public QueueDTO joinQueue(QueueDTO queueDTO) throws IllegalStateException {

        // 检查当前时间是否在允许的排队时间内
        //LocalTime currentTime = LocalTime.now();
        LocalTime currentTime = LocalTime.of(9, 30);
        if (currentTime.isBefore(START_TIME) || currentTime.isAfter(END_TIME)) {
            throw new IllegalStateException(
                    String.format("当前时间%s不在排队时间内(9:00-16:40)", currentTime.toString())
            );
        }
        // 检查等待队列是否已满
        int waitingCount = getWaitingCount(queueDTO.getCounselorId());
        if (waitingCount >= MAX_WAITING) {
            throw new IllegalStateException("等待队列已满，最多允许" + MAX_WAITING + "人等待");
        }
        
        //每天第一个患者加入时，lastNumber 为 null，队列号会重置为 1，实现每日队列的独立性。
        // 获取并锁定当前最大队列号
        Integer lastNumber = queueMapper.selectLastQueueNumberForUpdate(queueDTO.getCounselorId());
        int newNumber = (lastNumber != null) ? lastNumber + 1 : 1;

        Queue queue = new Queue();
        queue.setQueueNumber(newNumber);
        queue.setUserNumber(queueDTO.getUserNumber());
        queue.setUserId(queueDTO.getUserId());
        queue.setCounselorId(queueDTO.getCounselorId());
        queue.setQueueStatus("waiting");
        queue.setJoinQueueTime(LocalDateTime.now());

        queueMapper.insert(queue);
        return convertToDTO(queue);
    }


    @Override
    public QueuePositionDTO getQueuePosition(Integer queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) {
            return new QueuePositionDTO("排队记录不存在");
        }

        Integer aheadCount = queueMapper.countAheadUsers(
                queue.getCounselorId(),
                queue.getQueueNumber(),
                queue.getJoinQueueTime()
        );

        return new QueuePositionDTO(
                queueId,
                queue.getQueueNumber(),
                aheadCount,
                aheadCount * 20, // 预估每人20分钟
                "排队信息获取成功"
        );
    }

    // 其他Service方法实现...
    @Override
    public List<QueueDTO> getQueuesByUserId(Integer userId) {
        return queueMapper.selectByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueDTO> getCurrentQueue(Integer counselorId) {
        return queueMapper.selectCurrentQueue(counselorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateQueueStatus(Integer queueId, String status) {
        Queue queue = new Queue();
        queue.setQueueId(queueId);
        queue.setQueueStatus(status);
        queue.setExitQueueTime(LocalDateTime.now());
        return queueMapper.updateById(queue) > 0;
    }

    @Override
    public List<QueueDTO> getActiveQueues() {
        return queueMapper.selectActiveQueues().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QueueDTO callNextPatient(Integer counselorId) throws IllegalStateException {
        // 检查正在咨询的人数是否已达上限
        int inProgressCount = getInProgressCount(counselorId);
        if (inProgressCount >= MAX_IN_PROGRESS) {
            throw new IllegalStateException("正在咨询的人数已达上限" + MAX_IN_PROGRESS + "人");
        }

        Queue next = queueMapper.selectNextPatient(counselorId);
        if (next != null) {
            next.setQueueStatus("in_progress");
            queueMapper.updateById(next);
            return convertToDTO(next);
        }
        return new QueueDTO("没有等待中的患者");
    }
    @Override
    @Transactional
    public boolean cancelQueue(Integer queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue != null && "waiting".equals(queue.getQueueStatus())) {
            queue.setQueueStatus("cancelled");
            queue.setExitQueueTime(LocalDateTime.now());
            return queueMapper.updateById(queue) > 0;
        }
        return false;
    }


    @Override
    public int getWaitingCount(Integer counselorId) {
        return queueMapper.countByStatus(counselorId, "waiting");
    }

    @Override
    public int getInProgressCount(Integer counselorId) {
        return queueMapper.countByStatus(counselorId, "in_progress");
    }



    // === 私有方法 ===
    private QueueDTO convertToDTO(Queue queue) {
        QueueDTO dto = new QueueDTO();
        dto.setQueueId(queue.getQueueId());
        dto.setQueueNumber(queue.getQueueNumber());
        dto.setUserNumber(queue.getUserNumber());
        dto.setJoinQueueTime(formatDateTime(queue.getJoinQueueTime()));
        dto.setExitQueueTime(formatDateTime(queue.getExitQueueTime()));
        dto.setQueueStatus(queue.getQueueStatus());
        dto.setUserId(queue.getUserId());
        dto.setCounselorId(queue.getCounselorId());
        dto.setStatusMessage("操作成功");
        return dto;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                null;
    }
}