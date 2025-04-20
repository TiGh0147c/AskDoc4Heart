package com.example.demo.Schedule.service;
import java.util.stream.Collectors;


import com.example.demo.Leave.entity.LeaveApplication;
import com.example.demo.Leave.mapper.LeaveApplicationMapper;
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
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;
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
    /**
     * 根据请假状态更新排班状态
     * @param leaveId 请假ID
     * @return 更新结果
     */
   
    public int updateScheduleStatusByLeave(Integer leaveId) {
        // 1. 获取请假信息
        LeaveApplication leave = leaveApplicationMapper.selectById(leaveId);
        if (leave == null || !"approved".equals(leave.getLeaveStatus())) {
            return 0;
        }

        // 2. 更新关联的排班状态
        return scheduleMapper.updateScheduleStatus(leave.getScheduleId(), "on_leave");
    }
}