package com.example.demo.attendance.controller;

import com.example.demo.attendance.dto.AttendanceStatusDTO;
import com.example.demo.attendance.exception.AttendanceException;
import com.example.demo.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
