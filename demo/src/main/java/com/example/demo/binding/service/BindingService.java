package com.example.demo.binding.service;

import com.example.demo.binding.entity.BindingRecord;

/**
 * 绑定服务接口
 */
public interface BindingService {

    // 添加绑定记录
    int addBindingRecord(BindingRecord record);

}