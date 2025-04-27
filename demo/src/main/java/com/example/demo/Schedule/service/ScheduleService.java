package com.example.demo.Schedule.service;

import com.example.demo.Leave.entity.LeaveApplication;
import com.example.demo.Leave.mapper.LeaveApplicationMapper;
import com.example.demo.Schedule.entity.Schedule;
import com.example.demo.Schedule.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;

    public int addSchedule(Schedule schedule) {
        System.out.println("Inserting schedule with date: " + schedule.getDate());
        return scheduleMapper.insertSchedule(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleMapper.selectAll();
    }

    public Schedule getScheduleById(Integer scheduleId) {
        return scheduleMapper.selectById(scheduleId);
    }

    public List<Schedule> getSchedulesByDate(LocalDate date) {
        System.out.println("Querying schedules for date: " + date);
        return scheduleMapper.selectByDate(date);
    }

    public int updateSchedule(Schedule schedule) {
        return scheduleMapper.updateSchedule(schedule);
    }

    public int deleteSchedule(Integer scheduleId) {
        return scheduleMapper.deleteSchedule(scheduleId);
    }

    public List<Schedule> getSchedulesByCounselorId(Integer counselorId) {
        return scheduleMapper.selectByCounselorId(counselorId);
    }

    public List<Schedule> getSchedulesBySupervisorId(Integer supervisorId) {
        return scheduleMapper.selectBySupervisorId(supervisorId);
    }

    public int updateScheduleStatusByLeave(Integer leaveId) {
        LeaveApplication leave = leaveApplicationMapper.selectById(leaveId);
        if (leave == null || !"approved".equals(leave.getLeaveStatus())) {
            return 0;
        }
        return scheduleMapper.updateScheduleStatus(leave.getScheduleId(), "on_leave");
    }
}