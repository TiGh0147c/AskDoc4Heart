package com.example.demo.binding.controller;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.CounselorSupervisorBindingDTO;
import com.example.demo.binding.dto.SupervisorDTO;
import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.entity.Counselor;
import com.example.demo.binding.entity.Supervisor;
import com.example.demo.binding.service.BindingService;
import com.example.demo.profilemanagement.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/binding")
public class BindingController {

    @Autowired
    private BindingService bindingService;

    // 添加绑定记录
    @PostMapping("/add")
    public ResponseEntity<String> addBinding(@RequestBody BindingRecord record) {
        // 如果输入的咨询师id或督导id不存在，则返回错误提示
        if (bindingService.getCounselorById(record.getCounselorId()) == null
                || bindingService.getSupervisorById(record.getSupervisorId()) == null
        ) {
            return ResponseEntity.badRequest().body("咨询师或督导不存在");
        }

        int result = bindingService.addBindingRecord(record);

        if (result > 0) {
            // 插入成功
            return ResponseEntity.ok("Binding record added successfully!");
        } else if (result == -1) {
            // 重复操作（绑定或解绑）
            return ResponseEntity.badRequest().body("Binding status unchanged, no need to repeat the operation!");
        } else if (result == -2) {
            // 咨询师已经绑定别的督导
            return ResponseEntity.badRequest().body("The counselor is already bound to another supervisor and cannot be bound again!");
        } else if (result == -3) {
            // 咨询师ID对应的咨询师是督导，不能绑定督导
            return ResponseEntity.badRequest().body("A supervisor cannot bind another supervisor!");
        } else {
            // 插入失败
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add binding record!");
        }

    }

    // 根据咨询师ID获取绑定督导
    @GetMapping("/supervisor/{counselorId}")
    public ResponseEntity<?> getSupervisorByCounselorId(@PathVariable Integer counselorId) {
        // 如果咨询师不存在，返回错误提示
        if (bindingService.getCounselorById(counselorId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("咨询师不存在");
        }
        return ResponseEntity.ok(bindingService.getSupervisorByCounselorId(counselorId));
    }

    // 根据督导ID获取绑定咨询师
    @GetMapping("/counselors/{supervisorId}")
    public ResponseEntity<?> getCounselorsBySupervisorId(@PathVariable Integer supervisorId) {
        // 如果督导不存在，返回错误提示
        if (bindingService.getSupervisorById(supervisorId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("督导不存在");
        }
        return ResponseEntity.ok(bindingService.getCounselorsBySupervisorId(supervisorId));
    }

    // 获取所有咨询师和绑定督导信息
    @GetMapping("/all")
    public ResponseEntity<?> getAllBindings(HttpServletRequest request) throws IOException {
        // 检查是否有请求体（非法请求）
        if (request.getReader().ready()) {
            return ResponseEntity.badRequest().body("请求体需为空");
        }
        return ResponseEntity.ok(bindingService.getAllBindings());
    }

    // 获取所有咨询师信息（非督导）
    @GetMapping("/counselors")
    public ResponseEntity<?> getAllNonSupervisorCounselors() {
        List<Counselor> counselors = bindingService.getAllNonSupervisors();
        if (counselors == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("暂无咨询师（非督导）");
        }
        return ResponseEntity.ok(counselors);
    }

    // 获取所有督导信息
    @GetMapping("/supervisors")
    public ResponseEntity<?> getAllSupervisors() {
        List<Supervisor> supervisors = bindingService.getAllSupervisors();
        if (supervisors == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("暂无督导");
        }
        return ResponseEntity.ok(supervisors);
    }

}