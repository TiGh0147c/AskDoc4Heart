package com.example.demo.Leave.service;
import com.example.demo.Schedule.mapper.ScheduleMapper;
import com.example.demo.Leave.entity.LeaveApplication;
import com.example.demo.Leave.mapper.LeaveApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationService {
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;

    // 创建请假申请
    public int createLeaveApplication(LeaveApplication leaveApplication) {
        return leaveApplicationMapper.insertLeaveApplication(leaveApplication);
    }

    // 获取所有请假申请
    public List<LeaveApplication> getAllLeaveApplications() {
        return leaveApplicationMapper.selectAll();
    }

    // 根据ID获取请假申请
    public LeaveApplication getLeaveApplicationById(Integer leaveId) {
        return leaveApplicationMapper.selectById(leaveId);
    }

    // 获取申请人(咨询师或督导)的请假申请
    public List<LeaveApplication> getLeaveApplicationsByApplicant(Integer applicantId, String role) {
        return leaveApplicationMapper.selectByApplicant(applicantId, role);
    }

    // 更新请假状态
    public int updateLeaveStatus(Integer leaveId, String status) {
        return leaveApplicationMapper.updateLeaveStatus(leaveId, status);
    }

    // 删除请假申请
    public int deleteLeaveApplication(Integer leaveId) {
        return leaveApplicationMapper.deleteLeaveApplication(leaveId);
    }

    // 获取与排班关联的请假申请
    public List<LeaveApplication> getLeaveApplicationsByScheduleId(Integer scheduleId) {
        return leaveApplicationMapper.selectByScheduleId(scheduleId);
    }
}