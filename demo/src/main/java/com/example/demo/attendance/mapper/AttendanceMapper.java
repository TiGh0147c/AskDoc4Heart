package com.example.demo.attendance.mapper;

import com.example.demo.attendance.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {
    // 查询指定日期、角色、用户ID和时间段内的考勤记录
    List<AttendanceRecord> selectRecords(LocalDate today, String role, Integer userId, String timeSlot);

    // 插入一条考勤记录
    void insert(AttendanceRecord attendanceRecord);

    // 更新指定用户和角色的在线状态
    void updateOnDutyStatus(Integer userId, String role, Boolean onDuty);
}
