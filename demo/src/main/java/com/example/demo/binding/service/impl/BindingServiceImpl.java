package com.example.demo.binding.service.impl;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.SupervisorDTO;
import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.entity.Counselor;
import com.example.demo.binding.entity.Supervisor;
import com.example.demo.binding.mapper.BindingMapper;
import com.example.demo.binding.service.BindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 绑定服务实现类
 */
@Service
public class BindingServiceImpl implements BindingService {

    @Autowired
    private BindingMapper bindingMapper;

    // 添加绑定记录
    @Override
    public int addBindingRecord(BindingRecord record) {
        return bindingMapper.insertBindingRecord(record);
    }

    // 根据咨询师ID获取绑定的督导
    @Override
    public SupervisorDTO getSupervisorByCounselorId(int counselorId) {
        // 查询当前绑定的督导
        BindingRecord bindingRecord = bindingMapper.findLatestBindingByCounselorId(counselorId);

        System.out.println("绑定记录 = " + bindingRecord);
        if (bindingRecord != null && "bound".equals(bindingRecord.getBindingStatus())) {
            // 获取对应的督导信息
            Supervisor supervisor = bindingMapper.findSupervisorById(bindingRecord.getSupervisorId());
            return new SupervisorDTO(supervisor);
        }
        return null; // 没有找到绑定的督导，返回 null
    }

    // 根据督导ID获取绑定的咨询师
    public List<CounselorDTO> getCounselorsBySupervisorId(int supervisorId) {
        // 查询督导ID绑定的所有咨询师
        List<Counselor> counselors = bindingMapper.findCounselorsBySupervisorId(supervisorId);

        // 将 Counselor 实体转换为 DTO
        return counselors.stream()
                .map(counselor -> new CounselorDTO(counselor)) // 通过构造函数转换为 DTO
                .collect(Collectors.toList());

    }
}