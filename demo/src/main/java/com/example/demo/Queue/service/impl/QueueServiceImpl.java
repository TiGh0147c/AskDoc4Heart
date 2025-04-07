package com.example.demo.Queue.service.impl;

import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import com.example.demo.Queue.entity.Queue;
import com.example.demo.Queue.mapper.QueueMapper;
import com.example.demo.Queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueServiceImpl implements QueueService {

    @Autowired
    private QueueMapper queueMapper;

    @Override
    @Transactional
    public QueueDTO joinQueue(QueueDTO queueDTO) {
        LocalTime now= LocalTime.of(9, 30);
        //LocalTime now = LocalTime.now();
        LocalTime endTime = LocalTime.of(16, 40); // 下午4:40
        LocalTime startTime = LocalTime.of(8, 0); // 上午8:00

        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return new QueueDTO("当前时间不在服务时间内，无法加入队列。");
        }
        // 确保是当天的队列
        LocalDateTime nowDateTime = LocalDateTime.now();
        List<Queue> currentDayQueues = queueMapper.selectCurrentQueue(queueDTO.getCounselorId());
        boolean isNewDay = currentDayQueues.stream().noneMatch(q -> q.getJoinQueueTime().toLocalDate().equals(nowDateTime.toLocalDate()));

        if (isNewDay && !currentDayQueues.isEmpty()) {
            // 如果发现有不属于今天的队列条目，则清空这些条目
            queueMapper.clearQueueByCounselorId(queueDTO.getCounselorId());
        }
        // 检查当前咨询师的总排队人数是否已达上限
        int totalWaitingCount = queueMapper.countTotalWaitingUsers(queueDTO.getCounselorId());

        if (totalWaitingCount >= 10) {
            return new QueueDTO("当前咨询师的排队人数已达上限，无法加入队列。");
        }

        //生成新的编号
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

        // 检查并处理下一位患者
        processNextPatientIfCounselorAvailable(queueDTO.getCounselorId());

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
                queue.getJoinQueueTime(),
                Arrays.asList("waiting") // 只计算状态为waiting的用户
        );

        return new QueuePositionDTO(
                queueId,
                queue.getQueueNumber(),
                aheadCount,
                aheadCount * 50, // 预估每人50分钟
                "排队信息获取成功"
        );
    }

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
        boolean result = queueMapper.updateById(queue) > 0;

        // 获取咨询师ID
        Integer counselorId = queueMapper.selectById(queueId).getCounselorId();

        // 检查并处理下一位患者
        if ("completed".equals(status)) {
            processNextPatientIfCounselorAvailable(counselorId);
        }

        return result;
    }

    @Override
    public List<QueueDTO> getActiveQueues() {
        return queueMapper.selectActiveQueues().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QueueDTO callNextPatient(Integer counselorId) {
        Queue next = queueMapper.selectNextPatient(counselorId);
        if (next != null) {
            updateQueueStatus(next.getQueueId(), "processing");
            return convertToDTO(next);
        }
        return new QueueDTO("没有等待中的患者");
    }
    @Override
    @Transactional
    public void clearAllQueues() {
        queueMapper.clearAllQueues();
    }
    // === 私有方法 ===
    //检查当前正在被咨询的人数是否少于3人。
    //如果少于3人，则将队列中的第一位用户出队并标记为“正在处理”。
    private void processNextPatientIfCounselorAvailable(Integer counselorId) {
        int currentConsultingCount = queueMapper.selectCurrentQueue(counselorId).size();
        int totalWaitingCount = queueMapper.countTotalWaitingUsers(counselorId);
        if (currentConsultingCount < 3 && totalWaitingCount < 10) {
            Queue nextPatient = queueMapper.selectNextPatient(counselorId);
            if (nextPatient != null) {
                nextPatient.setQueueStatus("processing");
                queueMapper.updateById(nextPatient);
            }
        }
    }

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

