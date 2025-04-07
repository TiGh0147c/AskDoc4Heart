package com.example.demo.Appointment.entity;

import java.time.LocalDate;  // 用于日期（YYYY-MM-DD）
import lombok.Data;
@Data  // Lombok注解，自动生成getter/setter

public class Appointment {
    private Integer appointmentId;       // 预约ID
    private LocalDate appointmentDate; // 预约日期（YYYY-MM-DD）
    private String appointmentTime;   // 预约时段（上午/下午）
    private String appointmentStatus; // 状态：completed/expired/cancelled/scheduled
    private Integer userId;              // 用户ID
    private Integer counselorId;         // 咨询师ID
}