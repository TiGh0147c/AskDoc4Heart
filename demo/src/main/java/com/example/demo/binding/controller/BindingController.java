package com.example.demo.binding.controller;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.SupervisorDTO;
import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.service.BindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/binding")
public class BindingController {

    @Autowired
    private BindingService bindingService;

    // 添加绑定记录
    @PostMapping("/add")
    public ResponseEntity<BindingRecord> addBinding(@RequestBody BindingRecord record) {
//        System.out.println("接收到数据：" + record);
        int result = bindingService.addBindingRecord(record);

//        return result > 0 ? "Binding record added successfully!" : "Failed to add binding record.";

        if (result > 0) {
            // 插入成功
            return ResponseEntity.ok(record);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    // 根据咨询师ID获取绑定咨询师
    @GetMapping("/supervisor/{counselorId}")
    public ResponseEntity<SupervisorDTO> getSupervisorByCounselorId(@PathVariable int counselorId) {
        SupervisorDTO supervisorDTO = bindingService.getSupervisorByCounselorId(counselorId);

        if (supervisorDTO != null) {
            return ResponseEntity.ok(supervisorDTO);
        } else {
            // 如果没有找到督导，可以返回一个带有描述信息的DTO
            SupervisorDTO notFoundDTO = new SupervisorDTO();
            return ResponseEntity.ok(notFoundDTO);
        }
    }

    // 根据督导ID获取绑定咨询师
    @GetMapping("/counselors/{supervisorId}")
    public ResponseEntity<List<CounselorDTO>> getCounselorsBySupervisorId(@PathVariable int supervisorId) {
        List<CounselorDTO> counselors = bindingService.getCounselorsBySupervisorId(supervisorId);

        if (!counselors.isEmpty()) {
            return ResponseEntity.ok(counselors);
        } else {
            // 如果没有找到相关咨询师，返回200 OK和空列表
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

}