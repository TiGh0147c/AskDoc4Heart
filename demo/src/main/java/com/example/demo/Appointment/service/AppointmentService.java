package com.example.demo.Appointment.service;
import com.example.demo.Appointment.mapper.CounselorScheduleMapper;
import com.example.demo.Appointment.mapper.SupervisorScheduleMapper;
import java.time.LocalDate;
import com.example.demo.Appointment.dto.AppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {



    // 创建预约
    AppointmentDTO createAppointment(AppointmentDTO appointmentDTO);

    // 根据用户ID获取所有预约
    List<AppointmentDTO> getAppointmentsByUserId(Integer userId);

    // 根据督导ID获取所有预约
    List<AppointmentDTO> getAppointmentsByCounselorId(Integer counselorId);

    List<AppointmentDTO> getUncompletedAppointmentsByUserId(Integer userId);

    // 获取所有预约
    List<AppointmentDTO> getAllAppointments();

    // 取消预约
    boolean cancelAppointment(Integer appointmentId);

    // 更新预约状态（例如更改为已完成）
    boolean updateAppointmentStatus(Integer appointmentId, String newStatus);


}

// Below is the code of /src/main/java/com/example/demo/Appointment/service/AppointmentServiceImpl.java
