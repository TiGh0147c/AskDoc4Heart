package com.example.demo.binding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 督导实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supervisor {
    private Integer supervisorId;
    private String name;
    private String phoneNumber;
    private String avatar;
    private String password;
    private String email;
    private String counselorCertificate;
    private Boolean isSupervisor = true;
    private String status = "available";
    private String expertiseArea;
    private Boolean onDuty = false;
}
