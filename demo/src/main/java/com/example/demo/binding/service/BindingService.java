package com.example.demo.binding.service;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.SupervisorDTO;
import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.entity.Counselor;
import com.example.demo.binding.entity.Supervisor;

import java.util.List;

/**
 * 绑定服务接口
 */
public interface BindingService {

    // 添加绑定记录
    int addBindingRecord(BindingRecord record);

    // 根据咨询师ID获取绑定的督导
    SupervisorDTO getSupervisorByCounselorId(int counselorId);

    // 根据督导ID获取绑定的咨询师
    public List<CounselorDTO> getCounselorsBySupervisorId(int supervisorId);
}