package com.example.demo.binding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 绑定记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingRecord {
    private Integer bindingId;
    private Date bindingDate;
    private String bindingStatus;
    private Integer supervisorId;
    private Integer counselorId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
