package com.example.demo.Queue.controller;
import com.example.demo.Queue.service.impl.QueueServiceImpl;
import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import com.example.demo.Queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/join")
    public ResponseEntity<QueueDTO> joinQueue(@RequestBody QueueDTO queueDTO) {
        QueueDTO result = queueService.joinQueue(queueDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/position/{queueId}")
    public ResponseEntity<QueuePositionDTO> getPosition(@PathVariable Integer queueId) {
        QueuePositionDTO position = queueService.getQueuePosition(queueId);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QueueDTO>> getUserQueues(@PathVariable Integer userId) {
        List<QueueDTO> queues = queueService.getQueuesByUserId(userId);
        return ResponseEntity.ok(queues);
    }

    @GetMapping("/counselor/{counselorId}/current")
    public ResponseEntity<List<QueueDTO>> getCurrentQueue(@PathVariable Integer counselorId) {
        List<QueueDTO> queues = queueService.getCurrentQueue(counselorId);
        return ResponseEntity.ok(queues);
    }

    @PostMapping("/{queueId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Integer queueId,
            @RequestParam String status) {
        boolean success = queueService.updateQueueStatus(queueId, status);
        return success ?
                ResponseEntity.ok("状态更新成功") :
                ResponseEntity.badRequest().body("状态更新失败");
    }

    @PostMapping("/counselor/{counselorId}/next")
    public ResponseEntity<QueueDTO> callNext(@PathVariable Integer counselorId) {
        QueueDTO next = queueService.callNextPatient(counselorId);
        return ResponseEntity.ok(next);
    }
    @PostMapping("/{queueId}/cancel")
    public ResponseEntity<String> cancelQueue(@PathVariable Integer queueId) {
        boolean success = queueService.cancelQueue(queueId);
        return success ?
                ResponseEntity.ok("排队已取消") :
                ResponseEntity.badRequest().body("取消排队失败");
    }

    @GetMapping("/{counselorId}/stats")
    public ResponseEntity<String> getQueueStats(@PathVariable Integer counselorId) {
        int waiting = queueService.getWaitingCount(counselorId);
        int inProgress = queueService.getInProgressCount(counselorId);
        return ResponseEntity.ok(String.format("等待中: %d/%d, 咨询中: %d/%d",
                waiting, 10,
                inProgress, 3));
    }
}