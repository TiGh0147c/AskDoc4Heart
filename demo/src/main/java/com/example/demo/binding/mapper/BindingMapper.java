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
    BindingRecord findLatestBindingByCounselorId (int counselorId);

    // 根据咨询师ID查询咨询师信息
    Supervisor findSupervisorById(Integer supervisorId);

    // 根据督导ID查询所有绑定的咨询师
    List<Counselor> findCounselorsBySupervisorId(int supervisorId);

}
