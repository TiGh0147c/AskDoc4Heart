package com.example.demo.attendance.service;

import com.example.demo.attendance.dto.AttendanceStatusDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface AttendanceService {
    // 获取考勤状态(是否需要打卡上下班)
    AttendanceStatusDTO getAttendanceStatus(Integer userId, String role);

    // 打卡上班
    void checkIn(Integer userId, String role);

    // 打卡下班
    void checkOut(Integer userId, String role);

    // 获取指定用户的指定时间段的打卡状态：
    // 未打卡上班
    // 已打卡上班（准时或迟到）
    // 缺勤
    // 已完成打卡（已打卡上班并且已打卡下班，同时会给出上班是准时还是迟到）
    String getTimeSlotStatus(Integer userId, String role, LocalDate date, String timeSlot);
}
