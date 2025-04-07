package com.example.demo.Appointment.dto;


import com.example.demo.Appointment.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AppointmentDTO {
    private Integer appointmentId;
    private  String appointmentDate;
    private String appointmentTime;
    private String appointmentStatus;
    private Integer userId;
    private Integer counselorId;
    private String userName;        // 用户姓名
    private String counselorName;   // 咨询师姓名
    private String statusMessage;   // 状态消息字段

    // 根据实体类Appointment创建DTO
    public AppointmentDTO(Appointment appointment, String userName, String counselorName) {
        this.appointmentId = appointment.getAppointmentId();
        this.appointmentDate = String.valueOf(LocalDate.parse(appointment.getAppointmentDate().toString()));
        this.appointmentTime=appointment.getAppointmentTime().toString().toLowerCase();
        this.appointmentStatus = appointment.getAppointmentStatus().toString().toLowerCase();
        this.userId = appointment.getUserId();
        this.counselorId = appointment.getCounselorId();
        this.userName = userName; // 从外部传入的用户信息
        this.counselorName = counselorName; // 从外部传入的咨询师信息
        this.statusMessage = "Appointment found";  // 默认状态消息
    }

    // 默认构造器，适用于没有找到预约记录时返回的消息
    public AppointmentDTO() {
        this.statusMessage = "Appointment not found";  // 默认状态消息
    }
}
