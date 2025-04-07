package com.example.demo.binding.controller;

import com.example.demo.binding.dto.CounselorDTO;
import com.example.demo.binding.dto.CounselorSupervisorBindingDTO;
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
@RequestMapping("/api/binding")
public class BindingController {

    @Autowired
    private BindingService bindingService;

    // 添加绑定记录
    @PostMapping("/add")
    public ResponseEntity<String> addBinding(@RequestBody BindingRecord record) {
//        System.out.println("接收到数据：" + record);
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

    // 根据咨询师ID获取绑定咨询师
    @GetMapping("/supervisor/{counselorId}")
    public ResponseEntity<SupervisorDTO> getSupervisorByCounselorId(@PathVariable Integer counselorId) {
        return ResponseEntity.ok(bindingService.getSupervisorByCounselorId(counselorId));
    }

    // 根据督导ID获取绑定咨询师
    @GetMapping("/counselors/{supervisorId}")
    public ResponseEntity<List<CounselorDTO>> getCounselorsBySupervisorId(@PathVariable Integer supervisorId) {
        return ResponseEntity.ok(bindingService.getCounselorsBySupervisorId(supervisorId));
    }

    // 获取所有咨询师和绑定督导信息
    @GetMapping("/all")
    public ResponseEntity<List<CounselorSupervisorBindingDTO>> getAllBindings() {
        return ResponseEntity.ok(bindingService.getAllBindings());
    }
}