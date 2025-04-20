package com.example.demo.attendance.mapper;

import com.example.demo.attendance.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Mapper
@Component("scheduleMapper1")
public interface ScheduleMapper {
    // 查询指定日期、角色、用户ID和时间段对应的排班记录
    Schedule selectSchedule(LocalDate today, String role, Integer userId, String timeSlot);
}
