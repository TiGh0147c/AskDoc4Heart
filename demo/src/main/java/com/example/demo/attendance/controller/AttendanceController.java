package com.example.demo.attendance.controller;

import com.example.demo.attendance.dto.AttendanceStatusDTO;
import com.example.demo.attendance.exception.AttendanceException;
import com.example.demo.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 获取当前用户是否需要打卡（用于显示按钮）
    @GetMapping("/status")
    public ResponseEntity<?> getAttendanceStatus(@RequestParam("userId") Integer userId,
                                                 @RequestParam("role") String role) {
        // role = "counselor" or "supervisor"
        try {
            AttendanceStatusDTO status = attendanceService.getAttendanceStatus(userId, role);
            return ResponseEntity.ok(status);
        } catch (AttendanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // 返回400错误及错误消息
        }
    }

    // 打卡上班
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestParam("userId") Integer userId,
                                     @RequestParam("role") String role) {
        try {
            attendanceService.checkIn(userId, role);
            return ResponseEntity.ok("打卡上班成功");
        } catch (AttendanceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 打卡下班
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestParam("userId") Integer userId,
                                      @RequestParam("role") String role) {
        try {
            attendanceService.checkOut(userId, role);
            return ResponseEntity.ok("打卡下班成功");
        } catch (AttendanceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 获取指定用户的指定时间段的打卡状态：
    // 未打卡上班
    // 已打卡上班（准时或迟到）
    // 缺勤
    // 已完成打卡（已打卡上班并且已打卡下班，同时会给出上班是准时还是迟到）
    @GetMapping("/now-status")
    public ResponseEntity<?> getTimeSlotStatus(@RequestParam("userId") Integer userId,
                                               @RequestParam("role") String role,
                                               @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                               @RequestParam("timeSlot") String timeSlot) {
        // timeSlot 示例值："morning" 或 "afternoon"
        try {
            String status = attendanceService.getTimeSlotStatus(userId, role, date, timeSlot);
            return ResponseEntity.ok(status);  // 直接返回中文状态描述
        } catch (AttendanceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
