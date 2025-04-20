package com.example.demo.Leave.controller;
import com.example.demo.Schedule.service.ScheduleService;
import com.example.demo.Leave.dto.LeaveApplicationDTO;
import com.example.demo.Leave.entity.LeaveApplication;
import com.example.demo.Leave.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-applications")
public class LeaveApplicationController {
    @Autowired
    private LeaveApplicationService leaveApplicationService;
    @Autowired
    private ScheduleService scheduleService;
    // 创建请假申请
    @PostMapping("/create")
    public ResponseEntity<?> createLeaveApplication(@RequestBody LeaveApplication leaveApplication) {
        int result = leaveApplicationService.createLeaveApplication(leaveApplication);
        if (result > 0) {
            return ResponseEntity.ok().body("请假申请创建成功");
        } else {
            return ResponseEntity.badRequest().body("请假申请创建失败");
        }
    }

    // 获取所有请假申请(管理员用)
    @GetMapping("/all")
    public ResponseEntity<List<LeaveApplication>> getAllLeaveApplications() {
        return ResponseEntity.ok(leaveApplicationService.getAllLeaveApplications());
    }

    // 根据ID获取请假申请
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveApplicationById(@PathVariable Integer id) {
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationById(id);
        if (leaveApplication != null) {
            return ResponseEntity.ok(leaveApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 获取申请人(咨询师或督导)的请假申请
    @GetMapping("/applicant/{applicantId}/{role}")
    public ResponseEntity<List<LeaveApplication>> getLeaveApplicationsByApplicant(
            @PathVariable Integer applicantId, @PathVariable String role) {
        return ResponseEntity.ok(leaveApplicationService.getLeaveApplicationsByApplicant(applicantId, role));
    }

    // 更新请假状态(审批)
    @PutMapping("/update-status/{id}/{status}")
    public ResponseEntity<?> updateLeaveStatus(
            @PathVariable Integer id, @PathVariable String status) {
        int result = leaveApplicationService.updateLeaveStatus(id, status);
        if (result > 0) {
            return ResponseEntity.ok().body("请假状态更新成功");
        } else {
            return ResponseEntity.badRequest().body("请假状态更新失败");
        }
    }

    // 删除请假申请
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLeaveApplication(@PathVariable Integer id) {
        int result = leaveApplicationService.deleteLeaveApplication(id);
        if (result > 0) {
            return ResponseEntity.ok().body("请假申请删除成功");
        } else {
            return ResponseEntity.badRequest().body("请假申请删除失败");
        }
    }

    // 获取与排班关联的请假申请
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<LeaveApplication>> getLeaveApplicationsByScheduleId(
            @PathVariable Integer scheduleId) {
        return ResponseEntity.ok(leaveApplicationService.getLeaveApplicationsByScheduleId(scheduleId));
    }
    /**
     * 更新请假申请状态
     * @param leaveId 请假ID
     * @param status 新状态(approved/rejected)
     * @return 操作结果
     */
    @PutMapping("/{leaveId}/status")
    public ResponseEntity<?> updateLeaveApplicationStatus(
            @PathVariable Integer leaveId,
            @RequestParam String status) {

        if (!"approved".equalsIgnoreCase(status) && !"rejected".equalsIgnoreCase(status)) {
            return ResponseEntity.badRequest().body("无效的状态值");
        }

        // 1. 更新请假状态
        int result = leaveApplicationService.updateLeaveStatus(leaveId, status.toLowerCase());

        if (result > 0 && "approved".equalsIgnoreCase(status)) {
            // 2. 如果批准，更新关联排班状态
            int scheduleUpdate = scheduleService.updateScheduleStatusByLeave(leaveId);
            if (scheduleUpdate == 0) {
                return ResponseEntity.ok().body("请假已批准，但排班状态更新失败");
            }
            return ResponseEntity.ok().body("请假已批准，排班状态已更新");
        }

        return result > 0
                ? ResponseEntity.ok().body("请假状态更新成功")
                : ResponseEntity.badRequest().body("请假状态更新失败");
    }
}