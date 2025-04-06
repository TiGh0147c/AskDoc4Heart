package com.example.demo.profilemanagement.entity;

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
    private String counselorCertificate;
    private Boolean isSupervisor;
    private String status;
    private String expertiseArea;
    private Boolean onDuty;
}
