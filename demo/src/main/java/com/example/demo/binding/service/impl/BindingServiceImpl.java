package com.example.demo.binding.service.impl;

import com.example.demo.binding.entity.BindingRecord;
import com.example.demo.binding.mapper.BindingMapper;
import com.example.demo.binding.service.BindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}