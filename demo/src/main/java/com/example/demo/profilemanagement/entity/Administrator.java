package com.example.demo.profilemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 咨询师实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Administrator {
    private Integer administratorId;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String contactNumber;
    private String avatar;
    private String status;
    private Timestamp createdAt;
}
