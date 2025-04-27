package com.example.demo.Schedule.mapper;

import com.example.demo.Schedule.entity.Schedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    int insertSchedule(Schedule schedule);
    Schedule selectById(Integer scheduleId);
    List<Schedule> selectAll();
    List<Schedule> selectByDate(LocalDate date);
    int updateSchedule(Schedule schedule);
    int deleteSchedule(Integer scheduleId);
    List<Schedule> selectByCounselorId(Integer counselorId);
    List<Schedule> selectBySupervisorId(Integer supervisorId);
    int updateScheduleStatus(@Param("scheduleId") Integer scheduleId,
                             @Param("status") String status);
}