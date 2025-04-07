package com.example.demo.Queue.controller;

import com.example.demo.Queue.dto.QueueDTO;
import com.example.demo.Queue.dto.QueuePositionDTO;
import com.example.demo.Queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QueueController 类处理与队列相关的HTTP请求。
 * 使用 @RestController 注解表示这是一个Spring MVC控制器，
 * 其中方法直接返回原始对象，并自动将对象序列化为JSON格式。
 */
@RestController
@RequestMapping("/api/queues") // 定义了所有请求的基础路径
public class QueueController {

    @Autowired // 自动注入 QueueService 实例，用于访问业务逻辑层
    private QueueService queueService;

    /**
     * 处理加入队列的POST请求。
     * 接收一个包含用户信息的 QueueDTO 对象，并将其传递给服务层进行处理。
     * 返回加入队列的结果作为响应实体。
     */
    @PostMapping("/join")
    public ResponseEntity<QueueDTO> joinQueue(@RequestBody QueueDTO queueDTO) {
        QueueDTO result = queueService.joinQueue(queueDTO);
        return ResponseEntity.ok(result); // 成功时返回200状态码及结果
    }

    /**
     * 根据队列ID获取用户在队列中的位置。
     * 通过GET请求访问此端点，并提供队列ID作为路径变量。
     * 返回用户的队列位置详情。
     */
    @GetMapping("/position/{queueId}")
    public ResponseEntity<QueuePositionDTO> getPosition(@PathVariable Integer queueId) {
        QueuePositionDTO position = queueService.getQueuePosition(queueId);
        return ResponseEntity.ok(position);
    }

    /**
     * 获取指定用户的所有队列条目。
     * 通过GET请求访问此端点，并提供用户ID作为路径变量。
     * 返回该用户的所有队列条目列表。
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QueueDTO>> getUserQueues(@PathVariable Integer userId) {
        List<QueueDTO> queues = queueService.getQueuesByUserId(userId);
        return ResponseEntity.ok(queues);
    }

    /**
     * 获取指定咨询师当前的队列。
     * 通过GET请求访问此端点，并提供咨询师ID作为路径变量。
     * 返回该咨询师当前正在处理的队列列表。
     */
    @GetMapping("/counselor/{counselorId}/current")
    public ResponseEntity<List<QueueDTO>> getCurrentQueue(@PathVariable Integer counselorId) {
        List<QueueDTO> queues = queueService.getCurrentQueue(counselorId);
        return ResponseEntity.ok(queues);
    }

    /**
     * 更新队列条目的状态。
     * 通过POST请求访问此端点，并提供队列ID和新的状态字符串。
     * 根据更新是否成功返回相应的响应消息。
     */
    @PostMapping("/{queueId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Integer queueId,
            @RequestParam String status) {
        boolean success = queueService.updateQueueStatus(queueId, status);
        return success ?
                ResponseEntity.ok("状态更新成功") :
                ResponseEntity.badRequest().body("状态更新失败");
    }

    /**
     * 调用下一个队列条目（通常由咨询师使用）。
     * 通过POST请求访问此端点，并提供咨询师ID。
     * 返回下一个待处理的队列条目。
     */
    @PostMapping("/counselor/{counselorId}/next")
    public ResponseEntity<QueueDTO> callNext(@PathVariable Integer counselorId) {
        QueueDTO next = queueService.callNextPatient(counselorId);
        return ResponseEntity.ok(next);
    }
}