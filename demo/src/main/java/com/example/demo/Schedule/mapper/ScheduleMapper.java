package com.example.demo.Schedule.mapper;

import com.example.demo.Schedule.entity.Schedule;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    // 插入排班记录
    int insertSchedule(Schedule schedule);

    // 根据ID查询排班
    Schedule selectById(Integer scheduleId);

    // 查询所有排班
    List<Schedule> selectAll();


    // 根据日期查询排班
    List<Schedule> selectByDate(Date date);

    // 更新排班信息
    int updateSchedule(Schedule schedule);

    // 删除排班
    int deleteSchedule(Integer scheduleId);

    // 查询某咨询师的所有排班
    List<Schedule> selectByCounselorId(Integer counselorId);

    // 查询某督导安排的所有排班
    List<Schedule> selectBySupervisorId(Integer supervisorId);
    int updateScheduleStatus(@Param("scheduleId") Integer scheduleId,
                             @Param("status") String status);
}
