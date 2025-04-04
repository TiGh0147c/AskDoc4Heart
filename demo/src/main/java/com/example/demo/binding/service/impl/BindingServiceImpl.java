package com.example.demo.binding.service.impl;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.CounselorSupervisorBindingDTO;
import com.example.demo.binding.dto.SupervisorDTO;
import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.entity.Counselor;
import com.example.demo.binding.entity.Supervisor;
import com.example.demo.binding.mapper.BindingMapper;
import com.example.demo.binding.service.BindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        // 查询咨询师信息
        Counselor counselor = bindingMapper.getCounselorById(record.getCounselorId());

        // 若咨询师ID对应的咨询师是督导，则拒绝添加绑定记录
        if (counselor != null && counselor.getIsSupervisor()) {
            return -3; // 督导不能绑定别的督导
        }

        // 查询该咨询师当前的绑定督导（可能为空）
        Supervisor currentSupervisor = bindingMapper.getSupervisorByCounselorId(record.getCounselorId());

        // 若当前咨询师已有督导，则拒绝添加bound绑定记录（已有的督导和要添加的督导不是同一位）
        if (currentSupervisor != null
                && record.getBindingStatus().equals("bound")
                && !currentSupervisor.getSupervisorId().equals(record.getSupervisorId())
        ) {
            return -2; // 表示咨询师已有督导
        }

        // 查询该咨询师与该督导的最新绑定记录
        BindingRecord latestBinding = bindingMapper.getLatestBindingByCounselorAndSupervisor(
            record.getCounselorId(), record.getSupervisorId()
        );

        // 若该咨询师与该督导的绑定状态，与想要添加的绑定状态相同，则拒绝添加绑定记录（因为重复添加）
        // 若该咨询师与该督导无绑定记录，则拒绝添加unbound绑定记录（因为重复添加，本来就没绑）
        if (latestBinding != null && latestBinding.getBindingStatus().equals(record.getBindingStatus())
                || latestBinding == null && record.getBindingStatus().equals("unbound")) {
            return -1; // 表示重复添加
        }

        return bindingMapper.insertBindingRecord(record);
    }

    // 根据咨询师ID获取绑定的督导
    @Override
    public SupervisorDTO getSupervisorByCounselorId(Integer counselorId) {
        Supervisor supervisor = bindingMapper.getSupervisorByCounselorId(counselorId);
        if (supervisor != null) {
            return new SupervisorDTO(supervisor);
        }
        return new SupervisorDTO(); // 没有找到绑定的督导，返回一个带有描述信息的DTO（"Supervisor not found"）
    }

    // 根据督导ID获取绑定的咨询师
    public List<CounselorDTO> getCounselorsBySupervisorId(Integer supervisorId) {
        // 查询督导ID绑定的所有咨询师
        List<Counselor> counselors = bindingMapper.getCounselorsBySupervisorId(supervisorId);

        // 将 Counselor 实体转换为 DTO
        return counselors.stream()
                .map(counselor -> new CounselorDTO(counselor)) // 通过构造函数转换为 DTO
                .collect(Collectors.toList());

    }

    // 获取所有咨询师和他们绑定的督导
    public List<CounselorSupervisorBindingDTO> getAllBindings() {
        // 获取所有咨询师（非督导）
        List<Counselor> counselors = bindingMapper.getAllCounselors();

        List<CounselorSupervisorBindingDTO> result = new ArrayList<>();

        for (Counselor counselor : counselors) {
            // 获取每个咨询师绑定的督导
            Supervisor supervisor = bindingMapper.getSupervisorByCounselorId(counselor.getCounselorId());
            if (supervisor != null) {
                // 构造 DTO
                CounselorSupervisorBindingDTO dto = new CounselorSupervisorBindingDTO(
                        counselor.getCounselorId(),
                        counselor.getName(),
                        counselor.getPhoneNumber(),
                        supervisor.getSupervisorId(),
                        supervisor.getName(),
                        supervisor.getPhoneNumber()
                );
                System.out.println(supervisor);
                result.add(dto);
            } else {
                // 如果没有绑定的督导，对应督导记为空
                CounselorSupervisorBindingDTO dto = new CounselorSupervisorBindingDTO(
                        counselor.getCounselorId(),
                        counselor.getName(),
                        counselor.getPhoneNumber(),
                        null,
                        null,
                        null
                );
                result.add(dto);
            }
        }
        return result;
    }
}