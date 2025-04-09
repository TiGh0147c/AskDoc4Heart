package com.example.demo.binding.mapper;

import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.entity.Counselor;
import com.example.demo.binding.entity.Supervisor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 绑定记录 Mapper
 */
@Mapper
public interface BindingMapper {

    // 添加绑定记录
    int insertBindingRecord(BindingRecord record);

    // 根据咨询师ID查询最新的绑定记录
    BindingRecord getLatestBindingByCounselorId(Integer counselorId);

    // 根据咨询师ID查询绑定的督导
    Supervisor getSupervisorByCounselorId(Integer counselorId);

    // 根据督导ID查询督导信息
    Supervisor getSupervisorById(Integer supervisorId);

    // 根据督导ID查询所有绑定的咨询师
    List<Counselor> getCounselorsBySupervisorId(Integer supervisorId);

    // 根据咨询师ID和督导ID查询最新的绑定记录
    BindingRecord getLatestBindingByCounselorAndSupervisor(Integer counselorId, Integer supervisorId);

    // 根据咨询师ID查询咨询师信息
    Counselor getCounselorById(Integer counselorId);

    // 获取所有咨询师
    List<Counselor> getAllCounselors();

    // 获取所有咨询师信息（非督导）
    List<Counselor> getAllNonSupervisors();

    // 获取所有督导信息
    List<Supervisor> getAllSupervisors();
}
