package com.example.demo.binding.service;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.CounselorSupervisorBindingDTO;
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
    SupervisorDTO getSupervisorByCounselorId(Integer counselorId);

    // 根据督导ID获取绑定的咨询师
    List<CounselorDTO> getCounselorsBySupervisorId(Integer supervisorId);

    // 获取所有咨询师和他们绑定的督导
    List<CounselorSupervisorBindingDTO> getAllBindings();

    // 获取所有非督导的咨询师
    List<Counselor> getAllNonSupervisors();

    // 获取所有督导信息
    List<Supervisor> getAllSupervisors();

    Supervisor getSupervisorById(Integer supervisorId);

    Counselor getCounselorById(Integer counselorId);
}