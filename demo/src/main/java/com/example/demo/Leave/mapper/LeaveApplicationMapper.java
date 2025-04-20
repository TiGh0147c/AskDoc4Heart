package com.example.demo.Leave.mapper;

import com.example.demo.Leave.entity.LeaveApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LeaveApplicationMapper {
    // 插入请假申请
    int insertLeaveApplication(LeaveApplication leaveApplication);

    // 根据ID查询请假申请
    LeaveApplication selectById(Integer leaveId);

    // 查询所有请假申请
    List<LeaveApplication> selectAll();

    // 根据申请人ID和角色查询请假申请
    List<LeaveApplication> selectByApplicant(Integer applicantId, String role);

    // 更新请假申请状态
    int updateLeaveStatus(@Param("leaveId") Integer leaveId, @Param("status") String status);

    // 删除请假申请
    int deleteLeaveApplication(Integer leaveId);

    // 查询与排班关联的请假申请
    List<LeaveApplication> selectByScheduleId(Integer scheduleId);

}
