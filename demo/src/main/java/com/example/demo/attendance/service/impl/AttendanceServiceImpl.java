package com.example.demo.attendance.service.impl;

import com.example.demo.attendance.entity.AttendanceRecord;
import com.example.demo.attendance.entity.Schedule;
import com.example.demo.attendance.dto.AttendanceStatusDTO;
import com.example.demo.attendance.exception.AttendanceException;
import com.example.demo.attendance.mapper.AttendanceMapper;
import com.example.demo.attendance.mapper.ScheduleMapper;
import com.example.demo.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    // 获取打卡状态
    @Override
    public AttendanceStatusDTO getAttendanceStatus(Integer userId, String role) {

        // 获取今天日期
        LocalDate today = LocalDate.now();
        // 获取当前时间
        LocalTime now = LocalTime.now();

        // 判断当前是上午还是下午（12:00 之前为上午）
        String timeSlot = now.isBefore(LocalTime.NOON) ? "morning" : "afternoon";

        // 获取当前时间段的排班
        Schedule schedule = scheduleMapper.selectSchedule(today, role, userId, timeSlot);
        if (schedule == null) {
            throw new AttendanceException("当前时间段没有排班，无法打卡");
        }

        // 获取该时间段的上班/下班时间
        LocalTime checkInTime = getStartTime(timeSlot);
        LocalTime checkOutTime = getEndTime(timeSlot);

        // 获取当前时间段的打卡记录
        List<AttendanceRecord> records = attendanceMapper.selectRecords(today, role, userId, timeSlot);

        AttendanceRecord checkInRecord = records.stream()
                .filter(r -> r.getStatus().equals("check_in"))
                .findFirst().orElse(null);

        AttendanceRecord checkOutRecord = records.stream()
                .filter(r -> r.getStatus().equals("check_out"))
                .findFirst().orElse(null);

        AttendanceStatusDTO dto = new AttendanceStatusDTO();

        // 判断是否显示“打卡上班”按钮
        boolean nearStartTime = false;
        if (checkInTime != null) {
            nearStartTime = now.isAfter(checkInTime.minusMinutes(20));
        }
        boolean beforeEndTime = now.isBefore(checkOutTime);
        // 20分钟内到上班时间(或者已经到上班时间)，并且还没到下班时间，并且未打卡上班
        dto.setShouldCheckIn(nearStartTime && beforeEndTime && checkInRecord == null);

        // 判断是否显示“打卡下班”按钮
        // 已打卡上班，已到下班时间，并且未打卡下班
        dto.setShouldCheckOut(checkInRecord != null && now.isAfter(checkOutTime) && checkOutRecord == null);

        return dto;

    }

    // 打卡上班
    @Override
    public void checkIn(Integer userId, String role) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取当前日期
        LocalDate today = now.toLocalDate();
        // 获取当前时间
        LocalTime currentTime = LocalTime.now();

        // 判断当前是上午还是下午（12:00 之前为上午）
        String timeSlot = currentTime.isBefore(LocalTime.NOON) ? "morning" : "afternoon";
        // 检查是否已经打卡上班
        List<AttendanceRecord> existingRecords = attendanceMapper.selectRecords(today, role, userId, timeSlot);
        // 如果已有打卡上班的记录，抛出异常
        boolean hasCheckedOut = existingRecords.stream()
                .anyMatch(record -> "check_in".equals(record.getStatus()));
        if (hasCheckedOut) {
            throw new AttendanceException("您已打卡上班，无法重复打卡");
        }

        // 获取考勤状态
        AttendanceStatusDTO status = getAttendanceStatus(userId, role);
        // 如果不允许打卡上班，抛出异常
        if (!status.isShouldCheckIn()) {
            throw new AttendanceException("当前时间不允许打卡上班");
        }

        // 记录上班打卡信息
        AttendanceRecord attendanceRecord = getAttendanceRecord(userId, role, now);
        attendanceRecord.setStatus("check_in");  // 上班打卡
        // 判断是否迟到
        // 获取该时间段的上班/下班时间
        LocalTime startTime = getStartTime(timeSlot);
        if (now.toLocalTime().isAfter(startTime)) {
            attendanceRecord.setAttendanceStatus("late");  // 设置迟到打卡
        }

        // 插入打卡记录
        attendanceMapper.insert(attendanceRecord);
    }

    // 打卡下班
    @Override
    public void checkOut(Integer userId, String role) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取当前日期
        LocalDate today = now.toLocalDate();
        // 获取当前时间
        LocalTime currentTime = now.toLocalTime();

        // 判断当前是上午还是下午（12:00 之前为上午）
        String timeSlot = currentTime.isBefore(LocalTime.NOON) ? "morning" : "afternoon";

        // 查询已有打卡记录
        List<AttendanceRecord> existingRecords = attendanceMapper.selectRecords(today, role, userId, timeSlot);

        // 检查是否已经打卡下班
        boolean hasCheckedOut = existingRecords.stream()
                .anyMatch(record -> "check_out".equals(record.getStatus()));
        if (hasCheckedOut) {
            throw new AttendanceException("您已打卡下班，无法重复打卡");
        }

        // 获取考勤状态
        AttendanceStatusDTO status = getAttendanceStatus(userId, role);
        // 如果不允许打卡下班，抛出异常
        if (!status.isShouldCheckOut()) {
            throw new AttendanceException("不允许打卡下班");
        }

        // 构造下班打卡记录
        AttendanceRecord attendanceRecord = getAttendanceRecord(userId, role, now);
        attendanceRecord.setStatus("check_out");

        // 插入下班打卡记录
        attendanceMapper.insert(attendanceRecord);
    }


    private static AttendanceRecord getAttendanceRecord(Integer userId, String role, LocalDateTime now) {
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setRole(role);
        attendanceRecord.setCounselorId("counselor".equals(role) ? userId : null);  // 设置对应的咨询师ID
        attendanceRecord.setSupervisorId("supervisor".equals(role) ? userId : null);  // 设置对应的督导ID
        attendanceRecord.setCheckInTime(now);  // 设置打卡时间
        attendanceRecord.setAttendanceStatus("on_time");  // 设为准时打卡（假设此时为准时）
        return attendanceRecord;
    }

    // 获取该时间段的上班时间
    private static LocalTime getStartTime(String timeSlot) {
        if ("morning".equals(timeSlot)) {
            return LocalTime.of(8, 0);
        } else if ("afternoon".equals(timeSlot)){
            return LocalTime.of(14, 0);
        }
        return null;
    }

    // 获取该时间段的下班时间
    private static LocalTime getEndTime(String timeSlot) {
        if ("morning".equals(timeSlot)) {
            return LocalTime.of(11, 0);
        } else if ("afternoon".equals(timeSlot)) {
            return LocalTime.of(20, 0);
        }
        return null;
    }
}
