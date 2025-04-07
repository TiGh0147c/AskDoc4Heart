package com.example.demo.binding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询师实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Counselor {
    private Integer counselorId;
    private String name;
    private String avatar;
    private String phoneNumber;
    private String password;
    private String email;
    private String gender;
    private String counselorCertificate;
    private Boolean isSupervisor = false;
    private String status = "available";
    private String expertiseArea;
    private Boolean onDuty = false;
}
