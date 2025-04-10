package com.example.demo.Schedule.service;



import com.example.demo.Schedule.dto.ScheduleDTO;
import com.example.demo.Schedule.entity.Schedule;
import com.example.demo.Schedule.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleMapper scheduleMapper;

    // 添加排班
    public int addSchedule(Schedule schedule) {
        return scheduleMapper.insertSchedule(schedule);
    }

    // 获取所有排班
    public List<Schedule> getAllSchedules() {
        return scheduleMapper.selectAll();
    }

    // 根据ID获取排班
    public Schedule getScheduleById(Integer scheduleId) {
        return scheduleMapper.selectById(scheduleId);
    }

    // 根据日期获取排班
    public List<Schedule> getSchedulesByDate(Date date) {
        return scheduleMapper.selectByDate(date);
    }

    // 更新排班
    public int updateSchedule(Schedule schedule) {
        return scheduleMapper.updateSchedule(schedule);
    }

    // 删除排班
    public int deleteSchedule(Integer scheduleId) {
        return scheduleMapper.deleteSchedule(scheduleId);
    }

    // 获取咨询师的排班
    public List<Schedule> getSchedulesByCounselorId(Integer counselorId) {
        return scheduleMapper.selectByCounselorId(counselorId);
    }

    // 获取督导安排的排班
    public List<Schedule> getSchedulesBySupervisorId(Integer supervisorId) {
        return scheduleMapper.selectBySupervisorId(supervisorId);
    }
}