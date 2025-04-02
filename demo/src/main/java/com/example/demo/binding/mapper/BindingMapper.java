package com.example.demo.binding.mapper;

import com.example.demo.binding.entity.BindingRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 绑定记录 Mapper
 */
@Mapper
public interface BindingMapper {

    // 添加绑定记录
//    @Insert("INSERT INTO Binding_Record (binding_date, binding_status, supervisor_id, counselor_id, created_at, updated_at) " +
//            "VALUES (#{bindingDate}, #{bindingStatus}, #{supervisorId}, #{counselorId}, NOW(), NOW())")
//    @Options(useGeneratedKeys = true, keyProperty = "bindingId")
    int insertBindingRecord(BindingRecord record);
}
