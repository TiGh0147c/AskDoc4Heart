package com.example.demo.binding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 绑定记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingRecord {
    private Integer bindingId;
    private Timestamp createdAt;
    private String bindingStatus;
    private Integer supervisorId;
    private Integer counselorId;
}
