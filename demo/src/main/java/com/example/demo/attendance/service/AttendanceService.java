package com.example.demo.attendance.service;

import com.example.demo.attendance.dto.AttendanceStatusDTO;
import org.springframework.stereotype.Service;

@Service
public interface AttendanceService {
    // 获取考勤状态(是否需要打卡上下班)
    AttendanceStatusDTO getAttendanceStatus(Integer userId, String role);

    // 打卡上班
    void checkIn(Integer userId, String role);

    // 打卡下班
    void checkOut(Integer userId, String role);
}
