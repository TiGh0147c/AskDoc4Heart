package com.example.demo.binding.controller;

import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.service.BindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/binding")
public class BindingController {

    @Autowired
    private BindingService bindingService;

    // 添加绑定记录
    @PostMapping("/add")
    public String addBinding(@RequestBody BindingRecord record) {
        System.out.println("接收到数据：" + record);
        int result = bindingService.addBindingRecord(record);
        return result > 0 ? "Binding record added successfully!" : "Failed to add binding record.";
    }
}